package fr.nil.backedflow.services.files;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
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
    public FileEntity saveFileEntity(UUID id, File file, Folder folder) {
        FileEntity fileEntity = getFileEntityById(id).get();
        fileEntity.setId(UUID.randomUUID());
        fileEntity.setUploadedAt(Date.valueOf(LocalDate.now()));
        fileEntity.setExpiresAt(Date.valueOf(LocalDate.now().plus(expiryDate, ChronoUnit.DAYS)));
        fileEntity.setFileSize(file.length());
        fileEntity.setFileType(fileUtils.getFileExtension(file));
        fileEntity.setFilePath(fileUtils.getFilePathFromUserStorage(file,folder.getFolderOwner()));
        fileEntity.setFileName(file.getName());
        fileEntity.setArchive(fileUtils.isFileArchive(file));
        return fileRepository.save(fileEntity);

    }
    public Optional<FileEntity> getFileEntityById(UUID id) {
        return fileRepository.findById(id);
    }

    public File getFileById(UUID id,Folder folder) {
        Optional<FileEntity> fileEntity = getFileEntityById(id);
        File file = new File(fileEntity.get().getFilePath());

    return file;
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
