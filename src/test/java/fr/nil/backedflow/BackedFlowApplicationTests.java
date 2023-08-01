package fr.nil.backedflow;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.FileRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FileUtils;
import fr.nil.backedflow.services.utils.FolderUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class BackedFlowApplicationTests {

    public Logger logger = LoggerFactory.getLogger(BackedFlowApplicationTests.class);
    @Autowired
    private FileEncryptorDecryptor fileEncryptorDecryptor;

    private FileUtils fileUtils;


    @BeforeEach
    @Test
    void initializeEntities()
    {
        fileUtils = new FileUtils();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testRandomURL()
    {
        logger.debug("Checking the URL generator");
        Assertions.assertNotNull(FolderUtils.generateRandomURL());
    }

    @Test
    void testAccessKeyGenerator()
    {
        logger.debug("Checking the Access Key generator");
        System.out.println(AccessKeyGenerator.generateAccessKey(32));
        Assertions.assertNotNull(AccessKeyGenerator.generateAccessKey(32));

    }

    @Test
    public void testGetFileExtension() {
        File testFile = new File("src/test/postman/testFile.txt");

        Assertions.assertEquals("txt", fileUtils.getFileExtension(testFile));
    }

    @Test
    public void testEncryptAndDecryptFile() throws IOException {
        File input = File.createTempFile("testFile", ".txt");
        File encrypted = File.createTempFile("encryptedTestFile", ".txt");
        File decrypted = File.createTempFile("decryptedTestFile", ".txt");

        // Write some content to the input file
        try (FileWriter writer = new FileWriter(input)) {
            writer.write("This is a test file.");
        }

        fileEncryptorDecryptor.encryptFile(input, encrypted);
        fileEncryptorDecryptor.decryptFile(encrypted, decrypted);

        Assertions.assertArrayEquals(Files.readAllBytes(input.toPath()), Files.readAllBytes(decrypted.toPath()));

        // Clean up the temp files
        input.deleteOnExit();
        encrypted.deleteOnExit();
        decrypted.deleteOnExit();
    }

}