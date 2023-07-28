package fr.nil.backedflow.services.folder;

import java.util.Random;

public class FolderUtils {


    public static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        // Create an instance of Random class
        Random random = new Random();

        // Generate random index and append corresponding character to the string builder
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        // Return the generated random string
        return sb.toString();
    }
}
