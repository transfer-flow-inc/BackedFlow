package fr.nil.backedflow.manager;

import fr.nil.backedflow.entities.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StorageManager {

    @Value("${TRANSFERFLOW_FILE_VAULT_MAIN_DIRECTORY}")
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

    public String getFilePathFromUserStorage(File file, User user)
    {
        return getUserFileStoragePath(user) + File.separator + file.getName();
    }


    public long checkStorageSize() throws IOException {
        return Files.walk(Paths.get(storagePath))
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
    }

    public long checkUserStorageSize(User user) throws IOException {
        Path userFolderPath = Paths.get(storagePath, "user_" + user.getId());
        return Files.walk(userFolderPath)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
    }
/*
    public boolean canUserUpload(User user) throws IOException {
        long size = checkUserStorageSize(user);
        return user.get().getMaxUploadCapacity() > size;
    }

 */
}