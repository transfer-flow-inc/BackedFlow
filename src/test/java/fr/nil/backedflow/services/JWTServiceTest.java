package fr.nil.backedflow.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void whenExtractUsernameFromToken_thenUsernameReturned() {
        // Given
        String token = "token";
        String username = "username";
        Claims claims = new DefaultClaims(Map.of(Claims.SUBJECT, username));
        JWTService jwtService = spy(JWTService.class);
        doReturn(claims).when(jwtService).extractAllClaims(token);

        // When
        String extractedUsername = jwtService.extractUsernameFromToken(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testGenerateToken() {
        String username = "testUsername";
        when(userDetails.getUsername()).thenReturn(username);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
    }
}