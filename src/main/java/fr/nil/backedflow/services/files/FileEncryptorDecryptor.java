package fr.nil.backedflow.services.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
            byte[] encryptedContent = StreamUtils.copyToByteArray(fileInputStream);

            SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecretKey.getBytes(), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedContent = cipher.doFinal(encryptedContent);
            System.out.println(Arrays.toString(decryptedContent));
            outputStream.write(decryptedContent);
            System.out.println(outputStream);
        } catch (Exception e) {
            // Handle exceptions appropriately
            logger.error(String.format("An error occurred during the file upload, Error message : %s", e.getMessage()));
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    // Other helper methods, if needed
}