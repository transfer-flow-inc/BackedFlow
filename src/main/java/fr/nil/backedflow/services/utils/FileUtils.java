package fr.nil.backedflow.services.utils;

import fr.nil.backedflow.entities.user.User;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

    @Value("${transferflow.storage.vault.directory}")
    private String mainDirectoryPath;

    public String getFileStoragePath()
    {
        return mainDirectoryPath;
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

    public String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public boolean isFileArchive(File file) {
        String fileName = file.getName();
        String fileExtension = getFileExtension(new File(fileName));

        // Set of common archive file extensions
        Set<String> archiveExtensions = new HashSet<>();
        archiveExtensions.add("zip");
        archiveExtensions.add("rar");
        archiveExtensions.add("tar");
        archiveExtensions.add("gz");
        archiveExtensions.add("7z");

        return archiveExtensions.contains(fileExtension.toLowerCase());
    }

    public static Float convertSizeBytesToGB(long bytes) {
        double gb = bytes / Math.pow(1024, 3);
        DecimalFormat df = new DecimalFormat("#.000"); // Set your desired format here.
        return Float.parseFloat(df.format(gb));
    }
}
