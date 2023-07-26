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

	@Test
	void checkFileEncryption() throws NoSuchAlgorithmException {
		FileUtils fileUtils = new FileUtils();
		User user = userRepository.findUserById(UUID.fromString("de78ee60-2173-4f63-9704-bd2dbd377b86")).get();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256); // Set the key size to 256 bits
		File file = new File("/home/nil/IdeaProjects/BackedFlow/src/test/postman/collection.json");
		//fileEncryptorDecryptor.encryptFile(file,new File(fileUtils.getFilePathFromUserStorage(file,user) + File.separator + file.getName()));
		Folder folder = Folder.builder()
				.id(UUID.randomUUID())
				.folderOwner(user)
				.folderSize(1854L)
				.folderViews(1)
				.folderName("test")
				.fileCount(1)
				.url("test")
				.isShared(true)
				.isPrivate(false)
				.recipientsEmails(List.of("malhomme.ds@djijoijusq.com"))
				.build();
		folderRepository.save(folder);

		FileEntity fileEntity = FileEntity.builder()
				.id(UUID.randomUUID())
				.fileName(file.getName())
				.uploadedAt(Date.valueOf(LocalDate.now()))
				.expiresAt(Date.valueOf(LocalDate.now())).folder(folder).fileSize(file.length()).fileType(fileUtils.getFileExtension(file)).filePath(fileUtils.getFilePathFromUserStorage(file,folder.getFolderOwner())).isArchive(false).build();
		fileEntityRepository.save(fileEntity);
		fileService.saveFileToStorage(fileEntity.getId(),file,folder);
	}

}
