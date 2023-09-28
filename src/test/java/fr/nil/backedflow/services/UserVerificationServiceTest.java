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

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testCheckVerificationToken_InvalidToken() {
        String verificationToken = "invalidToken";

        when(userVerificationRepository.findUserVerificationByVerificationToken(verificationToken))
                .thenReturn(Optional.empty());

        boolean result = userVerificationService.checkAccountVerificationToken(verificationToken);

        assertFalse(result);
        verify(userVerificationRepository, never()).deleteById(any());
    }

    @Test
    public void testCheckVerificationToken_ValidToken() {
        String verificationToken = "validToken";
        UserVerification userVerification = mock(UserVerification.class);
        User user = mock(User.class);

        when(userVerificationRepository.findUserVerificationByVerificationToken(verificationToken))
                .thenReturn(Optional.of(userVerification));
        when(userVerification.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

        boolean result = userVerificationService.checkAccountVerificationToken(verificationToken);

        assertTrue(result);
        verify(userVerificationRepository, times(1)).deleteById(userVerification.getId());
    }

}
