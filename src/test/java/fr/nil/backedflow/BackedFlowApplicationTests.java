package fr.nil.backedflow;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.FileRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import fr.nil.backedflow.services.utils.FolderUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.UUID;

@SpringBootTest
class BackedFlowApplicationTests {

    public Logger logger = LoggerFactory.getLogger(BackedFlowApplicationTests.class);
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testRandomURL()
    {
        logger.info("Checking the URL generator");
        Assertions.assertNotNull(FolderUtils.generateRandomURL());
    }

    @Test
    void testAccessKeyGenerator()
    {
        logger.info("Checking the Access Key generator");
        System.out.println(AccessKeyGenerator.generateAccessKey(32));
        Assertions.assertNotNull(AccessKeyGenerator.generateAccessKey(32));

    }

    @Test
    void checkTotalStorageSize()
    {
        StorageManager storageManager = new StorageManager();

        try {
            System.out.println(storageManager.checkStorageSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void addFileToFolder()
    {
        FileEntity file = fileRepository.findById(UUID.fromString("06a5b9a0-e7e7-4dc0-b8c9-8d35d146f629")).get();
        Folder folder = folderRepository.findById(UUID.fromString("85a03fa0-8810-442c-8e3c-588c1b5e2171")).get();
        folder.setAccessKey(AccessKeyGenerator.generateAccessKey(32));
        //folder.getFileEntityList().add(file);

        folderRepository.save(folder);
    }
}
