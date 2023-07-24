package fr.nil.backedflow.services;


import fr.nil.backedflow.auth.exceptions.InvalidSSOLoginRequest;
import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.GoogleSSOLoginRequest;
import fr.nil.backedflow.auth.requests.RegisterRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.user.Role;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user with the provided information.
     *
     * @param request the information required to register the user
     * @return an authentication response containing a JWT token
     */
    public AuthenticationResponse register(RegisterRequest request)
    {

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mail(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authMethod", "spring_database");
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("userEmail", user.getMail());
        extraClaims.put("userRole", user.getRole());
        extraClaims.put("userID", user.getId());

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(extraClaims,user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Authenticates a user with the provided email and password.
     *
     * @param request the authentication request containing the user's email and password
     * @return an authentication response containing a JWT token
     * @throws BadCredentialsException if the provided email and password do not match a user in the user repository
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request)
    {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        User user = userRepository.findByMail(request.getEmail())
                .orElseThrow();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authMethod", "spring_database");
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("userEmail", user.getMail());
        extraClaims.put("userRole", user.getRole());
        extraClaims.put("userID", user.getId());

        String jwtToken = jwtService.generateToken(extraClaims,user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    //@Value("${pumper.api.auth.sso.google.client.id}")
    private String googleSSOClientID;

    /**
     * Authenticates a user using Google SSO.
     *
     * @param request the Google SSO login request containing the user's information
     * @return an authentication response containing a JWT token
     * @throws InvalidSSOLoginRequest if the provided SSO login request is invalid
     */

    public AuthenticationResponse authenticateViaSSO(GoogleSSOLoginRequest request)
    {

        // Check if the Azp is equals to the stored clientID, to avoid faking a SSO login to the app via another google sso login
        if(!Objects.equals(request.getAzp(), googleSSOClientID))
            throw new InvalidSSOLoginRequest();
        if(!Objects.equals(request.getAzp(), request.getAud()))
            throw new InvalidSSOLoginRequest();

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mail(request.getEmail())
                .password(passwordEncoder.encode(request.getJti()))
                .role(Role.USER)
                .build();

        if(!userRepository.existsByMail(request.getEmail()))
            userRepository.save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authMethod", "google_sso");
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("userEmail", user.getMail());
        extraClaims.put("userRole", user.getRole());
        extraClaims.put("userID", user.getId());
        String jwtToken = jwtService.generateToken(extraClaims,user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
