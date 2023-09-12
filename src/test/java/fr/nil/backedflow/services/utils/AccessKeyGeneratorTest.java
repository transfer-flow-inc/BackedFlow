package fr.nil.backedflow.services.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessKeyGeneratorTest {

    @Test
    void testGenerateAccessKeyWithLength() {
        int length = 15;
        String result = AccessKeyGenerator.generateAccessKey(length);
        assertEquals(length, result.length());

        for (char c : result.toCharArray()) {
            assertTrue(AccessKeyGenerator.CHARACTERS.contains(Character.toString(c)));
        }
    }

    @Test
    void testGenerateAccessKeyWithoutLength() {
        int expectedLength = 128;
        String result = AccessKeyGenerator.generateAccessKey();
        assertEquals(expectedLength, result.length());

        for (char c : result.toCharArray()) {
            assertTrue(AccessKeyGenerator.CHARACTERS.contains(Character.toString(c)));
        }
    }

    @Test
    void testGenerateVerificationToken() {
        int expectedLength = 64;
        String result = AccessKeyGenerator.generateVerificationToken();
        assertEquals(expectedLength, result.length());

        for (char c : result.toCharArray()) {
            assertTrue(AccessKeyGenerator.CHARACTERS.contains(Character.toString(c)));
        }
    }

}