package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.plan.Plan;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.repositories.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Spy
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private Environment env;

    private User user;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = User.builder().id(id).firstName("test").lastName("test").password("oldPassword").build();

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
    void testUpdateUser() throws PasswordMismatchException {
        // Create the necessary objects for the test
        String mail = "user@example.com";
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        String oldPassword = "oldPassword";


        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("user@example.com");
        authenticationRequest.setPassword("oldPassword");

        when(user.getMail()).thenReturn(mail);
        when(user.getPassword()).thenReturn(oldPassword);
        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        Counter counter = mock(Counter.class);

        when(counter.count()).thenReturn(1.0);
        when(meterRegistry.counter(anyString(), anyString())).thenReturn(counter);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(counter);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken(email, oldPassword));
        when(user.getPlan()).thenReturn(mock(Plan.class));


        when(authenticationService.authenticate(authenticationRequest))
                .thenReturn(AuthenticationResponse.builder().token("token").build());

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(AuthenticationResponse.builder().token("token").build());


        // Use assertions to verify the expected behavior
        assertNotNull(userService.updateUser(mail, updateRequest, oldPassword));

        // ...
    }

    @Test
    public void testUpdateUser2() throws PasswordMismatchException {
        String mail = "test@example.com";
        String oldPassword = "oldPassword";
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("John");

        User user = new User();
        user.setMail(mail);
        user.setFirstName("Alice");
        user.setPassword(passwordEncoder.encode(oldPassword));

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("user@example.com");
        authenticationRequest.setPassword("oldPassword");

        when(userRepository.findByMail(mail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse("jwtToken"));
        when(user.getPlan()).thenReturn(mock(Plan.class));

        AuthenticationResponse response = userService.updateUser(mail, updateRequest, oldPassword);

        assertEquals("jwtToken", response.getToken());
        assertEquals("John", user.getFirstName());

        verify(userRepository, times(1)).findByMail(mail);
        verify(passwordEncoder, times(1)).matches(oldPassword, user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void updateUser_ValidInput_UpdatesUser() {
        // Arrange
        User user = new User();
        user.setFirstName("OldFirst");
        user.setPassword("oldPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("NewFirst");
        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        userService.updateUser("test@mail.com", updateRequest, "oldPassword");

        // Assert
        assertEquals("NewFirst", user.getFirstName());
        verify(userRepository).save(user);
    }
}