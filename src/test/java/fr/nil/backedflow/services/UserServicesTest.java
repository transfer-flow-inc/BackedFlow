package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.plan.Plan;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserVerification;
import fr.nil.backedflow.event.AccountCreationEvent;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.repositories.PlanRepository;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserVerificationRepository;
import fr.nil.backedflow.responses.UserStorageResponse;
import fr.nil.backedflow.services.files.FileService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServicesTest {

    private final String email = "test@test.com";
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    private UUID id = UUID.randomUUID();


    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private FileService fileService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private StorageManager storageManager;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UserVerificationRepository userVerificationRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private UserVerificationService userVerificationService;

    @Mock
    private Logger logger;

    @Mock
    private Environment env;

    @Mock
    private HttpServletRequest request;

    private User user;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        id = UUID.randomUUID();
        user = new User();
        user.setId(id);
        user.setMail(email);
        user.setRole(Role.USER);
        user.setPassword("password");
    }


    @Test
    void testCreateUser() {
        // Given
        User user = new User();
        user.setMail("test@test.com");
        user.setPassword("password");

        // When userRepository.save(user) is called, return the user
        when(userRepository.save(user)).thenReturn(user);

        // Then
        User result = userService.createUser(user);

        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserById() {
        id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testGetUserByMail() {

        when(userRepository.findByMail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByMail(email);

        assertEquals(user, result);
        verify(userRepository, times(1)).findByMail(email);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserWhenUserNotFound() {
        String mail = "test@mail.com";
        when(userRepository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUser(mail, new UserUpdateRequest(), null);
        });
    }

    @Test
    void testUpdateUserWhenPasswordDoesNotMatch() {
        String mail = "test@mail.com";
        User user = new User();
        user.setPassword("encodedPassword");
        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(PasswordMismatchException.class, () -> {
            userService.updateUser(mail, new UserUpdateRequest(), "oldPassword");
        });
    }

    // Add similar tests for other branches such as when updating firstName, lastName, mail, and password

    @Test
    void testUpdateUserWhenUpdatingMail() {
        String mail = "test@mail.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword("oldPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newMail@mail.com");
        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(kafkaTemplate.send(anyString(), any(AccountCreationEvent.class))).thenReturn(null);

        when(userVerificationService.generateVerificationToken(any(User.class))).thenReturn(new UserVerification().builder().verificationToken("testoken").user(user).id(UUID.randomUUID()).build());

        // Mock the behavior of authenticationService.authenticate
        AuthenticationResponse mockResponse = AuthenticationResponse.builder().token("mockToken").build();
        when(authenticationService.authenticate(any())).thenReturn(mockResponse);

        userService.updateUser(mail, updateRequest, "oldPassword");

        assertEquals("newMail@mail.com", user.getMail());
        assertFalse(user.getIsAccountVerified());
    }

    @Test
    void testUpdateUserWhenUpdatingFirstName() {
        String mail = "test@mail.com";
        User user = new User();
        user.setPassword("oldPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Test");

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Mock the behavior of authenticationService.authenticate
        AuthenticationResponse mockResponse = AuthenticationResponse.builder().token("mockToken").build();
        when(authenticationService.authenticate(any())).thenReturn(mockResponse);

        userService.updateUser(mail, updateRequest, "oldPassword");

        assertEquals("Test", user.getFirstName());
    }

    @Test
    void testUpdateUserWhenUpdatingLastName() {
        String mail = "test@mail.com";
        User user = new User();
        user.setPassword("oldPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setLastName("Test");

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Mock the behavior of authenticationService.authenticate
        AuthenticationResponse mockResponse = AuthenticationResponse.builder().token("mockToken").build();
        when(authenticationService.authenticate(any())).thenReturn(mockResponse);

        userService.updateUser(mail, updateRequest, "oldPassword");

        assertEquals("Test", user.getLastName());
    }

    @Test
    void testUpdateUserWhenUpdatingPassword() {
        String mail = "test@mail.com";
        User user = new User();
        user.setPassword("oldPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPassword("oldPassword");

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Mock the behavior of authenticationService.authenticate
        AuthenticationResponse mockResponse = AuthenticationResponse.builder().token("mockToken").build();
        when(authenticationService.authenticate(any())).thenReturn(mockResponse);

        userService.updateUser(mail, updateRequest, "oldPassword");

        assertFalse(passwordEncoder.matches("oldPassword", user.getPassword()));
    }

    @Test
    void deleteUserByEmailTest() {
        String email = "test@email.com";
        User mockUser = mock(User.class);
        Plan mockPlan = mock(Plan.class);
        List<Folder> mockFolders = Arrays.asList(mock(Folder.class), mock(Folder.class));


        when(userDetails.getUsername()).thenReturn("test@email.com");
        when(userDetails.getPassword()).thenReturn("test");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(true);
        when(userRepository.findByMail(email)).thenReturn(Optional.of(user));

        when(mockUser.getId()).thenReturn(UUID.randomUUID());
        when(mockUser.getPlan()).thenReturn(mockPlan);
        when(folderRepository.findAllByFolderOwner(UUID.randomUUID())).thenReturn(Optional.of(mockFolders));

        userService.deleteUserByEmail(email);

        verify(entityManager).flush();
        verify(userRepository).deleteByMail(email);
    }

    @Test
    void deleteUserByEmailTestWithDebugOn() {
        String email = "test@email.com";
        User mockUser = mock(User.class);
        Plan mockPlan = mock(Plan.class);
        List<Folder> mockFolders = Arrays.asList(mock(Folder.class), mock(Folder.class));


        when(userDetails.getUsername()).thenReturn("test@email.com");
        when(userDetails.getPassword()).thenReturn("test");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(true);
        when(userRepository.findByMail(email)).thenReturn(Optional.of(user));

        when(mockUser.getId()).thenReturn(UUID.randomUUID());
        when(mockUser.getPlan()).thenReturn(mockPlan);
        //when(folderRepository.findAllByFolderOwner(UUID.randomUUID())).thenReturn(Optional.of(mockFolders));
        when(folderRepository.findAllByFolderOwner(any(UUID.class))).thenReturn(Optional.of(mockFolders));
        when(logger.isDebugEnabled()).thenReturn(true);

        when(folderRepository.findAllByFolderOwner(any(UUID.class))).thenReturn(Optional.of(mockFolders));
        doNothing().when(userRepository).deleteByMail(any(String.class));

        userService.deleteUserByEmail(email);

        verify(entityManager).flush();
        verify(userRepository).deleteByMail(email);
    }

    @Test
    void getUserStorageInfoTest() {
        String userId = UUID.randomUUID().toString(); // Example UUID
        User mockUser = mock(User.class);
        Plan mockPlan = mock(Plan.class);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockUser));
        when(mockUser.getPlan()).thenReturn(mockPlan);
        //when(userService.getUserById(UUID.fromString(userId))).thenReturn(mockUser);
        when(storageManager.getFormattedUserStorageSize(mockUser)).thenReturn(50.0f); // Some example storage size
        when(mockPlan.getMaxUploadCapacity()).thenReturn(100); // Some example max storage size

        UserStorageResponse response = userService.getUserStorageInfo(userId);

        assertNotNull(response);
        assertEquals(50.0f, response.getUsedStorage());
        assertEquals(100.0f, response.getMaxStorage());
    }


    @Test
    void canUserUploadWhenVerifiedAndHasStorageTest() {
        User mockUser = mock(User.class);

        when(mockUser.getIsAccountVerified()).thenReturn(true);
        when(storageManager.hasEnoughStorageSize(mockUser)).thenReturn(true);

        assertTrue(userService.canUserUpload(mockUser));
    }

    @Test
    void canUserUploadWhenNotVerifiedTest() {
        User mockUser = mock(User.class);

        when(mockUser.getIsAccountVerified()).thenReturn(false);
        when(storageManager.hasEnoughStorageSize(mockUser)).thenReturn(true);

        assertFalse(userService.canUserUpload(mockUser));
    }

    @Test
    void canUserUploadWhenNoStorageTest() {
        User mockUser = mock(User.class);

        when(mockUser.getIsAccountVerified()).thenReturn(true);
        when(storageManager.hasEnoughStorageSize(mockUser)).thenReturn(false);

        assertFalse(userService.canUserUpload(mockUser));
    }


    @Test
    void testGetUserIDFromRequest() {
        // Given
        String authHeader = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNjk0NDU2Mjk5LCJleHAiOjE3MjU5OTI4OTksImF1ZCI6Ind3dy5leGFtcGxlLmNvbSIsInN1YiI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJmaXJzdE5hbWUiOiJ0ZXN0IiwibGFzdE5hbWUiOiJ0ZXN0IiwidXNlckVtYWlsIjoidGVzdEB0ZXN0LmZyIiwidXNlclJvbGUiOiJVU0VSIn0.d7-hpeyl-6Gmwp0drohJFMjJJbhos0Zz4uY3uv64Mow";
        UUID expectedUUID = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtService.extractClaim(anyString(), any())).thenReturn(expectedUUID.toString());

        // When
        UUID actualUUID = userService.getUserIDFromRequest(request);

        // Then
        assertEquals(expectedUUID, actualUUID);
    }


    @Test
    void addFolderToFolderListTest() {
        // Arrange
        User user = new User();
        user.setUserFolders(new ArrayList<>());
        Folder folder = new Folder();

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User updatedUser = userService.addFolderToFolderList(user, folder);

        // Assert
        assertTrue(updatedUser.getUserFolders().contains(folder));
        verify(userRepository, times(1)).save(user);
    }
}