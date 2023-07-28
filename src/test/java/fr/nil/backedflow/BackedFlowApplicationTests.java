package fr.nil.backedflow;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.files.FileUtils;
import org.junit.jupiter.api.Test;
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

	@Autowired
	public FileEncryptorDecryptor fileEncryptorDecryptor;

	@Autowired
	public FileService fileService;
	@Autowired
	private FolderRepository folderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FileEntityRepository fileEntityRepository;


	@Test
	void contextLoads() {
	}
/*
	@Test
	void checkFileEncryption() throws NoSuchAlgorithmException {


		FileUtils fileUtils = new FileUtils();
		User user = userRepository.findUserById(UUID.fromString("db7ee5da-741e-41ef-9a30-b18f8c31a103")).get();

		File file = new File("/home/nilm/Desktop/Projet/BackedFlow/src/test/postman/collection.json");
		//fileEncryptorDecryptor.encryptFile(file,new File(fileUtils.getFilePathFromUserStorage(file,user) + File.separator + file.getName()));
		Folder folder = folderRepository.findById(UUID.fromString("5b79ead4-553b-4fdc-aa36-54f95b67b128")).get();

		FileEntity fileEntity = FileEntity.builder()
				.id(UUID.randomUUID())
				.fileName(file.getName())
				.uploadedAt(Date.valueOf(LocalDate.now()))
				.expiresAt(Date.valueOf(LocalDate.now())).folder(folder).fileSize(file.length()).fileType(fileUtils.getFileExtension(file)).filePath(fileUtils.getFilePathFromUserStorage(file,folder.getFolderOwner())).isArchive(false).build();

		fileEntityRepository.save(fileEntity);
		fileService.saveFileToStorage(fileEntity.getId(),file,folder);
	}

/*
	@Test
	void checkFileDecryption()
	{
		FileUtils fileUtils = new FileUtils();
		Folder folder = folderRepository.findById(UUID.fromString("5b79ead4-553b-4fdc-aa36-54f95b67b128")).get();
		File encryptedFile= fileService.getFileById(UUID.fromString("f9376022-2303-473e-92a3-e7f2cdf32f1e"),folder);
		User user = userRepository.findUserById(folder.getFolderOwner().getId()).get();
		System.out.println(encryptedFile.getPath());
		File decryptedFile = new File(fileUtils.getUserFileStoragePath(user) +File.separator+ encryptedFile.getName().replace(".json",".decrypt"));
		System.out.println(decryptedFile.getAbsolutePath());
		fileEncryptorDecryptor.decryptFile(encryptedFile, decryptedFile);
		System.out.println("hi");
	}

 */
}
