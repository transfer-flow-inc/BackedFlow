package fr.nil.backedflow.services.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class FileEncryptorDecryptor {

    private final Logger logger = LoggerFactory.getLogger(FileEncryptorDecryptor.class);

    @Value("${TRANSFERFLOW_FILE_ENCRYPTION_KEY}")
    private String aesSecretKey;

    private static final String ENCRYPTION_ALGORITHM = "AES";

    public void encryptFile(File inputFile, File encryptedFile) {
        try (
                FileInputStream fileInputStream = new FileInputStream(inputFile);
                FileOutputStream outputStream = new FileOutputStream(encryptedFile)
        ) {
            byte[] fileContent = StreamUtils.copyToByteArray(fileInputStream);

            SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecretKey.getBytes(), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedContent = cipher.doFinal(fileContent);

            outputStream.write(encryptedContent);
        } catch (Exception e) {
            logger.error(String.format("An error occurred during the file upload, Error message : %s", e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    public void decryptFile(File encryptedFile, File decryptedFile) {
        try (
                FileInputStream fileInputStream = new FileInputStream(encryptedFile);
                FileOutputStream outputStream = new FileOutputStream(decryptedFile)
        ) {
            decryptFileContent(fileInputStream, outputStream);
        } catch (Exception e) {
            // Handle exceptions appropriately
            logger.error(String.format("An error occurred during the file upload, Error message : %s", e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    private void decryptFileContent(FileInputStream fileInputStream, FileOutputStream outputStream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedContent = StreamUtils.copyToByteArray(fileInputStream);

        SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecretKey.getBytes(), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedContent = cipher.doFinal(encryptedContent);
        outputStream.write(decryptedContent);
    }


    public File getDecryptedFile(File encryptedFile) {
        try {
            // Create a temporary file to store the decrypted content
            File decryptedFile = File.createTempFile("decrypted", ".tmp");

            try (
                    FileInputStream fileInputStream = new FileInputStream(encryptedFile);
                    FileOutputStream outputStream = new FileOutputStream(decryptedFile)
            ) {
                decryptFileContent(fileInputStream, outputStream);
            } catch (Exception e) {
                // Handle exceptions appropriately
                logger.error(String.format("An error occurred during the file decryption, Error message : %s", e.getMessage()));
                logger.debug(Arrays.toString(e.getStackTrace()));
            }

            return decryptedFile;
        } catch (IOException e) {
            logger.error(String.format("An error occurred while creating the temporary file, Error message : %s", e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    // Other helper methods, if needed
}