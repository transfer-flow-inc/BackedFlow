package fr.nil.backedflow;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserVerification;
import fr.nil.backedflow.repositories.FileRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserVerificationRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FileUtils;
import fr.nil.backedflow.services.utils.FolderUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class BackedFlowApplicationTests {

    public Logger logger = LoggerFactory.getLogger(BackedFlowApplicationTests.class);
    @Mock
    private FileRepository fileRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserVerificationRepository userVerificationRepository;

    @Autowired
    private FileEncryptorDecryptor fileEncryptorDecryptor;


    @InjectMocks
    private User user;

    private FileUtils fileUtils;
    @InjectMocks
    private FileEntity fileEntity;
    @InjectMocks
    private Folder folder;
    @InjectMocks
    private UserVerification userVerification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize your entities here
        fileUtils = new FileUtils();
    }
    @Test
    void testSaveUserToRepository() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = userRepository.save(user);
        Assertions.assertNotNull(savedUser);
    }

    @Test
    void testSaveFileEntityToRepository() {
        when(fileRepository.save(any(FileEntity.class))).thenReturn(fileEntity);
        FileEntity savedFileEntity = fileRepository.save(fileEntity);
        Assertions.assertNotNull(savedFileEntity);
    }

    @Test
    void testSaveFolderEntityToRepository() {
        when(folderRepository.save(any(Folder.class))).thenReturn(folder);
        Folder savedFolder = folderRepository.save(folder);
        Assertions.assertNotNull(savedFolder);
    }

    @Test
    void testSaveUserVerificationEntityToRepository() {
        when(userVerificationRepository.save(any(UserVerification.class))).thenReturn(userVerification);
        UserVerification savedUserVerification = userVerificationRepository.save(userVerification);
        Assertions.assertNotNull(savedUserVerification);
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
    void testGetFileExtension() {
        File testFile = new File("src/test/postman/testFile.txt");

        Assertions.assertEquals("txt", fileUtils.getFileExtension(testFile));
    }

    @Test
    void testEncryptAndDecryptFile() throws IOException {
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