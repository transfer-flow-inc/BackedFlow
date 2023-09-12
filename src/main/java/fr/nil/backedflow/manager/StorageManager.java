package fr.nil.backedflow.manager;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.StorageCalculationException;
import fr.nil.backedflow.services.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
@Slf4j
public class StorageManager {

    @Value("${transferflow.storage.vault.directory}")
    private String storagePath;

    public String getFileStoragePath()
    {
        return storagePath;
    }

    public String getTempStoragePath()
    {
        File tempPath = new File(getFileStoragePath() + File.separator + "temp");
        tempPath.mkdirs();
        return tempPath.getAbsolutePath();
    }

    public String getUserFileStoragePath(User user)
    {
        File userPath = new File(getFileStoragePath() + File.separator + "user_" + user.getId());
        userPath.mkdirs();
        return userPath.getAbsolutePath(); // Create the user directory path

    }

    public String getUserFileStoragePathToFolder(User user, Folder folder) {
        File userPath = new File(getFileStoragePath() + File.separator + "user_" + user.getId() + File.separator + folder.getId());
        userPath.mkdirs();
        return userPath.getAbsolutePath(); // Create the user directory path

    }


    public long checkStorageSize() {
        try (Stream<Path> files = Files.walk(Paths.get(storagePath))) {
            return files.filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
        } catch (IOException e) {
            throw new StorageCalculationException(e.getMessage());
        }
    }

    public long checkUserStorageSize(User user) {

        if (getUserFileStoragePath(user) == null) {
            return 0;
        }

        Path userFolderPath = Paths.get(storagePath, "user_" + user.getId());
        try (Stream<Path> files = Files.walk(userFolderPath)) {
            return files
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            throw new StorageCalculationException(e.getMessage());
        }
    }

    public Float getFormattedUserStorageSize(User user) {
        long size = checkUserStorageSize(user);

        return FileUtils.convertSizeBytesToGB(size);
    }


    public boolean hasEnoughStorageSize(User user) {
        long size = checkUserStorageSize(user);
        log.info("Byte size storage " + size);
        log.info(String.format("User %s storage size: %s", user.getId().toString(), FileUtils.convertSizeBytesToGB(size)));
        return user.getPlan().getMaxUploadCapacity() > FileUtils.convertSizeBytesToGB(size);
    }


}