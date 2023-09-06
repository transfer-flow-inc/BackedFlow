package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserVerification;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserVerificationServiceTest {

    @InjectMocks
    private UserVerificationService userVerificationService;

    @Mock
    private UserVerificationRepository userVerificationRepository;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckVerificationToken_InvalidToken() {
        // Mock behavior
        when(userVerificationRepository.findUserVerificationByVerificationToken(anyString())).thenReturn(Optional.empty());

        // Invoke the method
        boolean result = userVerificationService.checkVerificationToken("invalidToken");

        // Assert the result
        assertFalse(result);
    }

    @Test
    void testGenerateVerificationToken() {
        // Create a mock User object
        User user = new User();
        user.setId(UUID.randomUUID());

        // Mock behavior
        when(userVerificationRepository.save(any(UserVerification.class))).thenReturn(new UserVerification());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Invoke the method
        UserVerification result = userVerificationService.generateVerificationToken(user);

        // Assert the result
        assertNotNull(result);
        verify(userVerificationRepository, times(1)).save(any(UserVerification.class));
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void testVerifyUser_AccountAlreadyVerified() {
        // Create a mock User object
        User user = new User();
        user.setIsAccountVerified(true);

        // Invoke the method
        userVerificationService.verifyUser(user);

        // Verify the behavior
        verify(userRepository, never()).save(any(User.class));
    }

}
