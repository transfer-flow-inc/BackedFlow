package fr.nil.backedflow.services.utils;

import fr.nil.backedflow.exceptions.UtilityClassConstructorException;

import java.security.SecureRandom;

public class AccessKeyGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-$*";

    private AccessKeyGenerator() {
        throw new UtilityClassConstructorException();
    }
    public static String generateAccessKey(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public static String generateAccessKey() {
        StringBuilder stringBuilder = new StringBuilder(128);

        for (int i = 0; i < 128; i++) {
            stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public static String generateVerificationToken() {

        StringBuilder stringBuilder = new StringBuilder(64);
        for (int i = 0; i < 64; i++) {
            stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return stringBuilder.toString();
    }
}