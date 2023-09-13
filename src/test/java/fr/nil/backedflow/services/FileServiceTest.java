package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.FileEntityRepository;
import fr.nil.backedflow.repositories.FileRepository;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.utils.FileUtils;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;


class FileServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private FileEntityRepository fileEntityRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileEncryptorDecryptor fileEncryptorDecryptor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Logger logger;

    @Mock
    private Files files;

    @InjectMocks
    private FileService fileService;

    private FileUtils fileUtils = new FileUtils();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        User user = User.builder().id(UUID.randomUUID()).firstName("test").lastName("test").build();

    }

    @Test
    void testSaveFileToStorage() {
        File mockFile = mock(File.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getFolderOwner()).thenReturn(mock(User.class));

        fileService.saveFileToStorage(mockFile, mockFolder);

        verify(fileEncryptorDecryptor, times(1)).encryptFile(any(File.class), any(File.class));
    }


    @Test
    void testIsFileExpired() {
        FileEntity mockFileEntity = new FileEntity();
        mockFileEntity.setExpiresAt(LocalDateTime.now().plusDays(-1));

        boolean isExpired = fileService.isFileExpired(mockFileEntity);

        assertFalse(isExpired);
    }


    @Test
    void testGetAllFiles() {
        List<FileEntity> mockFileEntityList = Arrays.asList(mock(FileEntity.class), mock(FileEntity.class));
        when(fileRepository.findAll()).thenReturn(mockFileEntityList);

        List<FileEntity> returnedFileEntityList = fileRepository.findAll();

        verify(fileRepository, times(1)).findAll();
        assertEquals(mockFileEntityList, returnedFileEntityList);
    }

    @Test
    public void testDeleteFilesFromUserStorage() throws IOException, IOException {
        Folder mockFolder = mock(Folder.class);
        FileEntity mockFileEntity = mock(FileEntity.class);

        when(mockFileEntity.getFileName()).thenReturn("testFile.txt");
        when(mockFileEntity.getFilePath()).thenReturn("/tmp/testFile.txt");

        List<FileEntity> mockFileEntityList = Arrays.asList(mockFileEntity);
        when(mockFolder.getFileEntityList()).thenReturn(mockFileEntityList);
        File mockFile = mock(File.class);
        Path mockPath = mock(Path.class);
        Files.createFile(Path.of(mockFileEntity.getFilePath()));
        when(mockFile.toPath()).thenReturn(mockPath);
        when(mockFolder.getFileEntityList()).thenReturn(Arrays.asList(mockFileEntity));
        when(mockFileEntityList.remove(any(FileEntity.class))).thenReturn(true);
        doReturn(true).when(mockFileEntityList.removeAll(Arrays.asList(mockFileEntity)));

        fileService.deleteFilesFromUserStorage(mockFolder);

        verify(logger, times(1)).debug(String.format("Deleting file %s", mockFileEntity.getFileName()));
        verify(logger, times(1)).debug(String.format("File %s has been deleted", mockFileEntity.getFileName()));
        verify(folderRepository, times(1)).save(mockFolder);
        verify(fileRepository, times(1)).deleteAll(anyList());
    }
}
