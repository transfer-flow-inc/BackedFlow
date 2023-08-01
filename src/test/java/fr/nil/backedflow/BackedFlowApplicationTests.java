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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

@SpringBootTest
class BackedFlowApplicationTests {

    public Logger logger = LoggerFactory.getLogger(BackedFlowApplicationTests.class);
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileEncryptorDecryptor fileEncryptorDecryptor;


    private User user;
    private FileUtils fileUtils;
    private FileEntity fileEntity;
    private Folder folder;



    @BeforeEach
    void initializeEntities()
    {
        fileUtils = new FileUtils();
        logger.debug("Creating test user entity ...");
        user = User.builder()
                .id(UUID.fromString("db12b2d3-53d8-4075-a02e-ef578e11aea4")) // for test static uuid
                .firstName("test")
                .lastName("test")
                .mail("test")
                .password("test")
                .role(Role.USER) // Assuming Role.TEST exists
                .avatar("test")
                .isAccountVerified(false)
                .userFolders(new ArrayList<>())
                .build();
        userRepository.save(user);
        logger.debug("User entity has been created and can be used.");

        logger.debug("Creating test file entity ...");
        fileEntity = FileEntity.builder()
                .id(UUID.fromString("ea79888c-60a4-47f2-920e-c9c439eeca64"))
                .fileName("testFile")
                .filePath("/temp/testFile.txt")
                .fileType("txt")
                .fileSize(250L)
                .isArchive(false)
                .expiresAt(Date.valueOf(LocalDate.now().plusDays(7)))
                .uploadedAt(Date.valueOf(LocalDate.now()))
                .build();
        fileRepository.save(fileEntity);
        logger.debug("File entity has been created and can be used.");

        logger.debug("Creating folder entity");
        folder = Folder.builder()
                .id(UUID.fromString("c04e0fab-622e-436e-af8d-a13e9db1241f"))
                .folderName("Default")
                .folderOwner(user)
                .folderViews(0)
                .url(FolderUtils.generateRandomURL())
                .accessKey(AccessKeyGenerator.generateAccessKey(32))
                .uploaded_at(Date.valueOf(LocalDate.now()))
                .expires_at(Date.valueOf(LocalDate.now().plusDays(7)))
                .fileEntityList(new ArrayList<>())
                .isPrivate(false)
                .isShared(true)
                .build();
        folderRepository.save(folder);
    }




    @Test
    @Order(1)
    void testSaveFileEntityToRepository()
    {
        logger.debug("Saving test fileEntity entity into repository");
        Assertions.assertNotNull(fileRepository.save(fileEntity));
    }



    @Test
    void testIfFileEntityExistsInRepository()
    {
        logger.debug("Check if fileEntity exists in repository");
        Assertions.assertFalse(fileRepository.existsById(fileEntity.getId()));
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
