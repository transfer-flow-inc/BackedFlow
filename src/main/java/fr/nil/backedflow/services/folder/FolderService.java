package fr.nil.backedflow.services.folder;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.JWTService;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FolderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public ResponseEntity<?> handleMultipleFileUpload(MultipartFile[] files, HttpServletRequest request)
    {
    User user = userRepository.findUserById(UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer",""), claims -> claims.get("userID").toString()))).orElseThrow(  );

    List<FileEntity> fileEntities = new ArrayList<>();
    Folder folder = addFolderToDatabase(user);

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
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload files => " + e.getMessage());

        }

    }
        addFilesToFolder(folder, fileEntities);
        return ResponseEntity.ok("Successfully uploaded - " + fileEntities.size());
}

    public Folder addFolderToDatabase(User user) {
        Folder folder = Folder.builder()
                .id(UUID.randomUUID())
                .folderName("Default")
                .folderOwner(user)
                .folderViews(0)
                .url(FolderUtils.generateRandomURL())
                .accessKey(AccessKeyGenerator.generateAccessKey(32))
                .uploaded_at(Date.valueOf(LocalDate.now()))
                .expires_at(Date.valueOf(LocalDate.now().plusDays(7)))
                .isPrivate(false)
                .isShared(true)
                .build();
        folder.setFileEntityList(new ArrayList<>());
        return folderRepository.save(folder);
    }

    public Folder addFilesToFolder(Folder folder, List<FileEntity> files) {
        folder.getFileEntityList().addAll(files);
        folder.setFileCount(folder.getFileEntityList().size());
        folder.setFolderSize(folder.getFileEntityList().stream().mapToLong(FileEntity::getFileSize).sum());
        return folderRepository.save(folder);
    }
}
