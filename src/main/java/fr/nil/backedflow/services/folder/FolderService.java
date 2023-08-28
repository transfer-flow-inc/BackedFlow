package fr.nil.backedflow.services.folder;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.UserNotFoundException;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.reponses.FolderResponse;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.requests.FolderCreationRequest;
import fr.nil.backedflow.services.JWTService;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FolderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Data
@RequiredArgsConstructor
@Service
public class FolderService {

    private final StorageManager storageManager;

    private final FileEncryptorDecryptor fileEncryptorDecryptor;
    private final JWTService jwtService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    private Logger logger = LoggerFactory.getLogger(FolderService.class);

    @Value("${TRANSFERFLOW_FILE_EXPIRY_DATE:7}")
    private int expiryDate;


    @SneakyThrows
    public ResponseEntity<?> handleMultipleFileUpload(MultipartFile[] files, String folderURL, HttpServletRequest request)
    {
        if(folderURL == null)
            folderURL = FolderUtils.generateRandomURL();

    User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer",""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

    List<FileEntity> fileEntities = new ArrayList<>();
    Folder folder = addFolderToDatabase(user, folderURL);

        for (MultipartFile file : files) {
        if (file.isEmpty()) {
            continue;
        }

        try {
            // Save the file to a temporary location
            Path tempPath = Paths.get(storageManager.getTempStoragePath() + File.separator + file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Files.write(tempPath, bytes);

            // Encrypt the file and save to the final location
            Path finalPath = Paths.get(storageManager.getUserFileStoragePath(user) + File.separator + file.getOriginalFilename());
            fileEncryptorDecryptor.encryptFile(tempPath.toFile(), finalPath.toFile());

            // Delete the temporary file
            Files.delete(tempPath);

            fileEntities.add(fileService.addFileEntity(finalPath.toFile()));
        } catch (Exception e) {
            // Handle exceptions appropriately,
            logger.error("An error occurred during the file upload (Error message : " + e.getMessage() + ").");
            logger.debug(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body("Something went wrong during the file upload please try again later");

        }

    }
        addFilesToFolder(folder, fileEntities);
        return ResponseEntity.ok(FolderResponse.builder().folder(folder).accessKey(folder.getAccessKey()).build());
}

    @SneakyThrows
    public ResponseEntity<?> handleSingleFileUpload(MultipartFile file, @RequestParam UUID folderUUID, HttpServletRequest request) {
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        if (folderRepository.findById(folderUUID).isEmpty())
            ResponseEntity.badRequest().body("Can't find the folder by the requested UUID");

        Folder targetFolder = folderRepository.findById(folderUUID).get();

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("Can't upload an empty file (fileSize:" + file.getSize() + ")");

        try {
            // Save the file to a temporary location
            Path tempPath = Paths.get(storageManager.getTempStoragePath() + File.separator + file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Files.write(tempPath, bytes);

            // Encrypt the file and save to the final location
            Path finalPath = Paths.get(storageManager.getUserFileStoragePath(user) + File.separator + file.getOriginalFilename());
            fileEncryptorDecryptor.encryptFile(tempPath.toFile(), finalPath.toFile());

            // Delete the temporary file
            Files.delete(tempPath);

            addFileToFolder(targetFolder, fileService.addFileEntity(finalPath.toFile()));
        } catch (Exception e) {
            // Handle exceptions appropriately,
            logger.error("An error occurred during the file upload (Error message : " + e.getMessage() + ").");
            logger.debug(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body("Something went wrong during the file upload please try again later");

        }
        return ResponseEntity.ok(FolderResponse.builder().folder(targetFolder).accessKey(targetFolder.getAccessKey()).build());

    }

    @SneakyThrows
    public ResponseEntity<Folder> handleGetFolderURLRequest(String folderURL, HttpServletRequest request) {
        if (folderRepository.getFolderByUrl(folderURL).isEmpty())
            return ResponseEntity.notFound().build();

        Folder requestedFolder = folderRepository.getFolderByUrl(folderURL).get();
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        if (user.getRole().equals(Role.ADMIN))
            return ResponseEntity.ok(requestedFolder);

        if (!Objects.equals(requestedFolder.getFolderOwner().getId().toString(), user.getId().toString()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(requestedFolder);

    }

    @SneakyThrows
    public ResponseEntity<List<Folder>> getAllFolderByUserID(String userID, HttpServletRequest request) {
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        if (user.getRole().equals(Role.ADMIN))
            return ResponseEntity.ok(folderRepository.findAllByFolderOwner(UUID.fromString(userID)));
        if (!Objects.equals(user.getId().toString(), userID))
            return ResponseEntity.badRequest().build();


        return ResponseEntity.ok(folderRepository.findAllByFolderOwner(UUID.fromString(userID)));

    }

    public Folder addFolderToDatabase(User user, String folderURL) {
        Folder folder = Folder.builder()
                .id(UUID.randomUUID())
                .folderName("Default")
                .folderOwner(user)
                .folderViews(0)
                .url(folderURL)
                .accessKey(AccessKeyGenerator.generateAccessKey(32))
                .uploaded_at(Date.valueOf(LocalDate.now()))
                .expires_at(Date.valueOf(LocalDate.now().plusDays(7)))
                .isPrivate(false)
                .isShared(true)
                .build();
        folder.setFileEntityList(new ArrayList<>());
        return folderRepository.save(folder);
    }

    @SneakyThrows
    public Folder createEmptyFolder(FolderCreationRequest creationRequest, HttpServletRequest request) {

        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        logger.debug("Creating a new folder with the name : " + creationRequest.getFolderName() + " requested by userID : " + user.getId());

        return folderRepository.save(Folder.builder()
                .folderName(creationRequest.getFolderName())
                .folderOwner(user)
                .accessKey(AccessKeyGenerator.generateAccessKey())
                .isPrivate(false)
                .isShared(true)
                .folderViews(0)
                .message(creationRequest.getMessage())
                .uploaded_at(Date.valueOf(LocalDate.now()))
                .expires_at(Date.valueOf(LocalDate.now().plusDays(expiryDate)))
                .recipientsEmails(creationRequest.getRecipientsEmails())
                .url(FolderUtils.generateRandomURL())
                .build());


    }

    public Folder addFilesToFolder(Folder folder, List<FileEntity> files) {
        folder.getFileEntityList().addAll(files);
        folder.setFileCount(folder.getFileEntityList().size());
        folder.setFolderSize(folder.getFileEntityList().stream().mapToLong(FileEntity::getFileSize).sum());
        return folderRepository.save(folder);
    }

    public Folder addFileToFolder(Folder folder, FileEntity file) {
        folder.getFileEntityList().add(file);
        folder.setFileCount(folder.getFileEntityList().size());
        folder.setFolderSize(folder.getFileEntityList().stream().mapToLong(FileEntity::getFileSize).sum());
        return folderRepository.save(folder);
    }

    @SneakyThrows
    public ResponseEntity<Folder> handleGetFolderURLRequest(String folderURL, HttpServletRequest request) {
        if (folderRepository.getFolderByUrl(folderURL).isEmpty())
            return ResponseEntity.notFound().build();

        Folder requestedFolder = folderRepository.getFolderByUrl(folderURL).get();
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        if (user.getRole().equals(Role.ADMIN))
            return ResponseEntity.ok(requestedFolder);

        if (!Objects.equals(requestedFolder.getFolderOwner().getId().toString(), user.getId().toString()))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(requestedFolder);

    }

    @SneakyThrows
    public ResponseEntity<List<FolderResponse>> getAllFolderByUserID(String userID, HttpServletRequest request) {
        User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()))).orElseThrow(UserNotFoundException::new);

        List<FolderResponse> folderResponses = new ArrayList<>();
        folderRepository.findAllByFolderOwner(UUID.fromString(userID)).forEach(folder ->
                folderResponses.add(FolderResponse.builder().folder(folder).accessKey(folder.getAccessKey()).build()));

        if (user.getRole().equals(Role.ADMIN))
            return ResponseEntity.ok(folderResponses);
        if (!Objects.equals(user.getId().toString(), userID))
            return ResponseEntity.badRequest().build();


        return ResponseEntity.ok(folderResponses);

    }

}
