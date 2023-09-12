package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.UnauthorizedFolderCreationException;
import fr.nil.backedflow.exceptions.UserNotFoundException;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.folder.FolderService;
import fr.nil.backedflow.services.utils.FolderUtils;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FolderServiceTest {

    //Generate unitary test for the Folder.service class using Junit 5 and Mocktio

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderService folderService;

    @Mock
    private StorageManager storageManager;
    @Mock
    private FileEncryptorDecryptor fileEncryptorDecryptor;
    @Mock
    private JWTService jwtService;
    @Mock
    private FileService fileService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;
    @Mock
    private UserService userService;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleSingleFileUploadUserNotFound() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        when(userService.getUserIDFromRequest(mockRequest)).thenReturn(UUID.randomUUID());
        when(userRepository.findUserById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            folderService.handleSingleFileUpload(mockFile, UUID.randomUUID(), mockRequest);
        });
    }

    @Test
    void testHandleSingleFileUploadUserNoPermission() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        MultipartFile mockFile = mock(MultipartFile.class);
        User mockUser = mock(User.class);

        when(userService.getUserIDFromRequest(mockRequest)).thenReturn(UUID.randomUUID());
        when(userRepository.findUserById(any(UUID.class))).thenReturn(Optional.of(mockUser));
        when(userService.canUserUpload(mockUser)).thenReturn(false);

        assertThrows(UnauthorizedFolderCreationException.class, () -> {
            folderService.handleSingleFileUpload(mockFile, UUID.randomUUID(), mockRequest);
        });
    }


    @Test
    void testGetFolderByUrl() {
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getUrl()).thenReturn(FolderUtils.generateRandomURL());
        when(folderRepository.getFolderByUrl(any(String.class))).thenReturn(java.util.Optional.of(mockFolder));


        Folder result = folderRepository.getFolderByUrl(mockFolder.getUrl()).get();

        assertEquals(mockFolder, result);

    }

}
