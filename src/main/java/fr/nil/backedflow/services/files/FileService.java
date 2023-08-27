package fr.nil.backedflow.services.files;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.utils.FileUtils;
import fr.nil.backedflow.stats.MetricsEnum;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Data
@RequiredArgsConstructor
@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${TRANSFERFLOW_FILE_EXPIRY_DATE:7}")
    private int expiryDate = 7;

    private final MeterRegistry meterRegistry;

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
        meterRegistry.counter(MetricsEnum.FILE_TRANSFER_UPLOAD_COUNT.getMetricName()).increment();

        //  meterRegistry.gauge(MetricsEnum.FILE_TRANSFER_UPLOAD_SIZE.getMetricName(),file.length() != 0 ? file.length() / 1024.0 : 0);

        /*
        long fileSize = file.length();
        Gauge.builder(MetricsEnum.FILE_TRANSFER_UPLOAD_SIZE.getMetricName(), file, fileL -> fileSize / (1024.0 * 1024.0))
                .tag("fileID", String.valueOf(fileEntity.getId()))
               // .strongReference(true)
                .register(meterRegistry);



        Gauge.builder(MetricsEnum.FILE_TRANSFER_UPLOAD_SIZE.getMetricName(), () -> {
                    long fileSize = file.length(); // Get the current file size being uploaded
                    return fileSize / (1024.0 * 1024.0); // Convert to megabytes if needed
                })
                .register(meterRegistry);

        System.out.println(file.length());
*/
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

    public File getZippedFiles(List<FileEntity> fileEntities) throws IOException {
            // Create a buffer for reading the files
            byte[] buffer = new byte[1024];

            // Create a temp zip file
            File tempZip = File.createTempFile("files", ".zip");

            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempZip))) {
                // Compress the files
                for (FileEntity fileEntity : fileEntities) {
                    File originalFile = new File(fileEntity.getFilePath());
                    File decryptedFile = File.createTempFile("decrypted", ".tmp");

                    // Decrypt the file
                    fileEncryptorDecryptor.decryptFile(originalFile, decryptedFile);

                    // Open the input file
                    FileInputStream in = new FileInputStream(decryptedFile.getAbsoluteFile());

                    // Add ZIP entry to output stream
                    out.putNextEntry(new ZipEntry(fileEntity.getFileName()));

                    // Transfer bytes from the file to the ZIP file
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }

                    // Complete the entry
                    out.closeEntry();
                    in.close();

                    // Delete the decrypted temporary file
                    decryptedFile.delete();
                }
            } catch (IOException e) {
                logger.error("An error occurred during the file zipping (Error message : " + e.getMessage() + ").");
                logger.debug(Arrays.toString(e.getStackTrace()));
            }

            // Complete the ZIP file
            return tempZip;
        }

}
