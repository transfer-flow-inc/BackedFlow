package fr.nil.backedflow.services.files;

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

@Component
public class FileEncryptorDecryptor {

    @Value("${TRANSFERFLOW_FILE_ENCRYPTION_KEY}")
    private String aesSecretKey;

    private static final String ENCRYPTION_ALGORITHM = "AES";

    public void encryptFile(File inputFile, File encryptedFile) {
            try {
                byte[] fileContent = StreamUtils.copyToByteArray(new FileInputStream(inputFile));

                SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecretKey.getBytes(), ENCRYPTION_ALGORITHM);
                Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                byte[] encryptedContent = cipher.doFinal(fileContent);

                FileOutputStream outputStream = new FileOutputStream(encryptedFile);
                outputStream.write(encryptedContent);
                outputStream.close();
            } catch (Exception e) {
                // Handle exceptions appropriately
                throw new RuntimeException(e);
            }
    }

    public void decryptFile(File encryptedFile, File decryptedFile) {
        try {
            byte[] encryptedContent = StreamUtils.copyToByteArray(new FileInputStream(encryptedFile));

            SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecretKey.getBytes(), ENCRYPTION_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedContent = cipher.doFinal(encryptedContent);

            FileOutputStream outputStream = new FileOutputStream(decryptedFile);
            outputStream.write(decryptedContent);
            outputStream.close();
        } catch (Exception e) {
            // Handle exceptions appropriately
        }
    }

    // Other helper methods, if needed
}