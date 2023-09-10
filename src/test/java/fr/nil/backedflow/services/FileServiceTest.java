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

import java.io.File;
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


}
