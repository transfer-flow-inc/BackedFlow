package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JWTServiceTest {

    @Mock
    private UserDetails userDetails;

    private final String SECRET_KEY = "0F2EFBF39F874180F73CDD0A2A0E7F2E44C3F8859A7E6DFAB139D79E28BD3A5D!";

    @InjectMocks
    private JWTService jwtService;
    @Mock
    private UserRepository userRepository;
    private String jwtToken;

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        jwtService.secretKey = SECRET_KEY;
        User user = mock(User.class);
        when(userDetails.getUsername()).thenReturn("test");
        when(userDetails.getPassword()).thenReturn("test");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(true);
        jwtService.secretKey = SECRET_KEY;
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    void testAGenerateToken() {
        User user = mock(User.class);
        when(userDetails.getUsername()).thenReturn("test");
        when(userDetails.getPassword()).thenReturn("test");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(true);
        jwtService.secretKey = SECRET_KEY;
        jwtToken = jwtService.generateToken(userDetails);
        System.out.println(jwtToken);
        assertNotNull(jwtToken);

    }

    @Test
    void testExtractUsernameFromToken() {

        when(jwtService.extractUsernameFromToken(jwtToken)).thenReturn("expected_username");


        String username = jwtService.extractUsernameFromToken(jwtToken);

        assertEquals("test", username);
    }

    @Test
    void testIsTokenValid() {
        User user = mock(User.class);
        when(userDetails.getUsername()).thenReturn("test");
        when(userDetails.getPassword()).thenReturn("test");
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(true);

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findUserById(any(UUID.class))).thenReturn(Optional.of(user));

        HttpServletRequest request = mock(HttpServletRequest.class); // Provide a mock request if needed
        boolean isValid = jwtService.isTokenValid(jwtToken, userDetails, request);
        assertTrue(isValid);
    }


    @Test
    void testIsTokenExpired() {
        HttpServletRequest request = mock(HttpServletRequest.class); // Provide a mock request if needed

        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNjk0NDU4MzA0LCJleHAiOjE2OTQ0NDAzMDR9.VpFY4snwdXWNs4AGnqNHD08q8x6dtmV6pmJllu7YVAE";
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredJwtToken, userDetails, request));
    }

}

