package fr.nil.backedflow.services.folder;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.*;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.requests.FolderCreationRequest;
import fr.nil.backedflow.responses.FolderResponse;
import fr.nil.backedflow.services.JWTService;
import fr.nil.backedflow.services.KafkaService;
import fr.nil.backedflow.services.MeterService;
import fr.nil.backedflow.services.UserService;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FolderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


@RequiredArgsConstructor
@Service
public class FolderService {

    private final StorageManager storageManager;

    private final FileEncryptorDecryptor fileEncryptorDecryptor;
    private final JWTService jwtService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final Logger logger = LoggerFactory.getLogger(FolderService.class);
    private final UserService userService;
    private final MeterService meterService;
    private final KafkaService kafkaService;


    @Value("${TRANSFERFLOW_FILE_EXPIRY_DATE:7}")
    private int expiryDate;

    @SneakyThrows
    public ResponseEntity<FolderResponse> handleSingleFileUpload(MultipartFile file, @RequestParam UUID folderUUID, HttpServletRequest request) {
        User user = userRepository.findUserById(userService.getUserIDFromRequest(request)).orElseThrow(UserNotFoundException::new);

        if (!userService.canUserUpload(user))
            throw new UnauthorizedFolderCreationException();

        if (folderRepository.findById(folderUUID).isEmpty())
            throw new FolderNotFoundException();

        Folder targetFolder = folderRepository.findById(folderUUID).orElseThrow();

        if (file.isEmpty())
            throw new FileEmptyUploadException();

        try {
            // Save the file to a temporary location
            Path tempPath = Paths.get(storageManager.getTempStoragePath() + File.separator + file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Files.write(tempPath, bytes);
            // Encrypt the file and save to the final location
            Path finalPath = Paths.get(storageManager.getUserFileStoragePathToFolder(user, targetFolder) + File.separator + getUniqueFilename(file.getOriginalFilename(), user, targetFolder));


            fileEncryptorDecryptor.encryptFile(tempPath.toFile(), finalPath.toFile());
            meterService.updateUploadFileSizeGauge(finalPath.toFile().length());
            meterService.incrementFileUploadCounter();
            // Delete the temporary file
            Files.delete(tempPath);

            addFileToFolder(targetFolder, fileService.addFileEntity(finalPath.toFile()));
        } catch (Exception e) {
            // Handle exceptions appropriately,
            logger.error(String.format("An error occurred during the file upload (Error message : %s ).", e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
            throw new FileUploadException();

        }
        return ResponseEntity.ok(FolderResponse.builder().folder(targetFolder).accessKey(targetFolder.getAccessKey()).build());

    }

    @SneakyThrows
    public ResponseEntity<Folder> handleGetFolderURLRequest(String folderURL) {

        if (folderRepository.getFolderByUrl(folderURL).isEmpty())
            throw new FolderNotFoundException("The requested folder cannot be found by URL");

        Folder requestedFolder = folderRepository.getFolderByUrl(folderURL).orElseThrow();
        requestedFolder.setAccessKey(null);

        return ResponseEntity.ok(requestedFolder);
    }


    public Folder createEmptyFolder(FolderCreationRequest creationRequest, HttpServletRequest request) {

        User user = userRepository.findUserById(userService.getUserIDFromRequest(request)).orElseThrow(UserNotFoundException::new);

        if (!userService.canUserUpload(user))
            throw new UnauthorizedFolderCreationException();

        if (logger.isDebugEnabled())
            logger.debug(String.format("Creating a new folder with the name : %s requested by userID : %s", creationRequest.getFolderName(), user.getId()));

        Folder folder = folderRepository.save(Folder.builder()
                .folderName(creationRequest.getFolderName())
                .folderOwner(user)
                .folderSize(creationRequest.getFolderSize())
                .fileCount(creationRequest.getFileCount())
                .accessKey(AccessKeyGenerator.generateAccessKey())
                .isPrivate(false)
                .isShared(true)
                .folderViews(0)
                .message(creationRequest.getMessage())
                .uploadedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(expiryDate))
                .recipientsEmails(creationRequest.getRecipientsEmails())
                .url(FolderUtils.generateRandomURL())
                .build());

        userService.addFolderToFolderList(user, folder);

        logger.debug("Sending notification mail to all recipients");
        kafkaService.sendTransferNotification(user, folder);

        return folder;
    }

    public Folder addFileToFolder(Folder folder, FileEntity file) {
        folder.getFileEntityList().add(file);
        folder.setFileCount(folder.getFileEntityList().size());
        folder.setFolderSize(folder.getFileEntityList().stream().mapToLong(FileEntity::getFileSize).sum());
        return folderRepository.save(folder);
    }


    public void handleDeleteFolder(String folderID, HttpServletRequest request) {
        String userID = jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString());
        Folder folder = folderRepository.getReferenceById(UUID.fromString(folderID));
        User user = userRepository.findUserById(UUID.fromString(userID)).orElseThrow(UserNotFoundException::new);

        if (user.getRole().equals(Role.ADMIN))
            deleteFolder(folder);
        if (!Objects.equals(user.getId().toString(), userID))
            throw new UnauthorizedFolderAccessException();

        deleteFolder(folder);

    }


    public void deleteFolder(Folder folder) {

        User user = folder.getFolderOwner();

        user.getUserFolders().remove(folder);
        userRepository.save(user);
        fileService.deleteFilesFromUserStorage(folder);
        folderRepository.delete(folder);
    }

    public ResponseEntity<List<Folder>> getAllFolderByUserID(String userID, HttpServletRequest request) {
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);
        Optional<List<Folder>> folderList = folderRepository.findAllByFolderOwner(UUID.fromString(userID));

        if (folderList.isEmpty() || (folderList.get().isEmpty()))
            return ResponseEntity.ok(List.of(Folder.builder().build()));

        if (user.getRole().equals(Role.ADMIN))
            return ResponseEntity.ok(folderList.get());
        if (!Objects.equals(user.getId().toString(), userID))
            throw new UnauthorizedFolderAccessException();


        return ResponseEntity.ok(folderList.get());

    }


    public List<Folder> getExpiredFolders() {
        return folderRepository.findByExpiresAtBefore(LocalDateTime.now());
    }

    private String getUniqueFilename(String originalFilename, User user, Folder targetFolder) {
        // Check if a file with the same name already exists in the target folder
        File existingFile = new File(storageManager.getUserFileStoragePathToFolder(user, targetFolder) + File.separator + originalFilename);
        int count = 0;
        String newFilename = originalFilename;

        // If a file with the same name exists, append a unique identifier to the new file's name
        while (existingFile.exists()) {
            count++;
            String nameWithoutExtension = FilenameUtils.getBaseName(originalFilename);
            String extension = FilenameUtils.getExtension(originalFilename);
            newFilename = nameWithoutExtension + "(" + count + ")." + extension;
            existingFile = new File(storageManager.getUserFileStoragePathToFolder(user, targetFolder) + File.separator + newFilename);
        }

        return newFilename;
    }


}
