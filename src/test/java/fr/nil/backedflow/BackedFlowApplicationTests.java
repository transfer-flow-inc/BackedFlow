package fr.nil.backedflow;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FileRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.files.FileUtils;
import fr.nil.backedflow.services.folder.FolderUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.awt.*;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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
        Assertions.assertNotNull(FolderUtils.generateRandomString());
    }


    @Test
    void addFileToFolder()
    {
        FileEntity file = fileRepository.findById(UUID.fromString("a745d4f3-0085-4ced-a53c-1c532d3b2bf0")).get();
        Folder folder = folderRepository.findById(UUID.fromString("5b79ead4-553b-4fdc-aa36-54f95b67b128")).get();

        folder.getFileEntityList().add(file);

        folderRepository.save(folder);
    }
}
