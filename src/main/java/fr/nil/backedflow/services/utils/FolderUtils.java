package fr.nil.backedflow.services.utils;

import fr.nil.backedflow.exceptions.UtilityClassConstructorException;

import java.security.SecureRandom;

public class FolderUtils {

    private static final SecureRandom random = new SecureRandom();


    private FolderUtils() {
        throw new UtilityClassConstructorException();
    }
    public static String generateRandomURL() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        // Create an instance of Random class

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
