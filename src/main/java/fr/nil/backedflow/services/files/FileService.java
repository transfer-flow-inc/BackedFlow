package fr.nil.backedflow.services.files;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.utils.FileUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;


import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${TRANSFERFLOW_FILE_EXPIRY_DATE}")
    private int expiryDate;

    private final FileEntityRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FileUtils fileUtils = new FileUtils();
    @Autowired
    public FileEncryptorDecryptor fileEncryptorDecryptor;
    private final UserRepository userRepository;

    // Save file to storage as well as adding the FileEntity to DB

    public void saveFileToStorage(UUID id,File file, Folder folder) {
        fileEncryptorDecryptor.encryptFile(file,new File(fileUtils.getFilePathFromUserStorage(file,folder.getFolderOwner())));
    }

    // Updates the FileEntity to the DB
    public FileEntity addFileEntity(File file) {
        FileEntity fileEntity = FileEntity.builder()
        .id(UUID.randomUUID())
        .uploadedAt(Date.valueOf(LocalDate.now()))
        .expiresAt(Date.valueOf(LocalDate.now().plusDays(expiryDate)))
        .fileSize(file.length())
        .fileType(fileUtils.getFileExtension(file))
        .filePath(file.getAbsolutePath())
        .fileName(file.getName())
        .isArchive(fileUtils.isFileArchive(file))
                .build();
        return fileRepository.save(fileEntity);

    }
    public Optional<FileEntity> getFileEntityById(UUID id) {
        return fileRepository.findById(id);
    }

    public File getFileById(UUID id,Folder folder) {
        Optional<FileEntity> fileEntity = getFileEntityById(id);

        return new File(fileEntity.get().getFilePath());
    }

    public boolean isFileExpired(FileEntity fileEntity)
    {
        return fileEntity.getExpiresAt().after(Date.valueOf(LocalDate.now()));
    }
    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(UUID id) {
        fileRepository.deleteById(id);
    }
}
