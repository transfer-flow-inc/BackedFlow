package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServicesTest {

    private final String email = "test@test.com";
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user;
    private UUID id = UUID.randomUUID();

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
}