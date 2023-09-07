package fr.nil.backedflow.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JWTServiceTest {

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JWTService jwtService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateToken() {
        String username = "testUsername";
        when(userDetails.getUsername()).thenReturn(username);
        JWTService jwtService = spy(JWTService.class);
        doReturn("atestoken").when(jwtService).generateToken(any(UserDetails.class));
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
    }
}