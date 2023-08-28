package fr.nil.backedflow.auth;


import fr.nil.backedflow.auth.exceptions.EmailAlreadyUsedException;
import fr.nil.backedflow.auth.exceptions.InvalidEmailException;
import fr.nil.backedflow.auth.exceptions.InvalidUsernameException;
import fr.nil.backedflow.auth.exceptions.UnsecuredPasswordException;
import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.GoogleSSOLoginRequest;
import fr.nil.backedflow.auth.requests.RegisterRequest;
import fr.nil.backedflow.auth.requests.TokenRefreshRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class represents the controller for the authentication API endpoints.
 *
 * @param authenticationService The authentication service used to handle user authentication requests.
 * @param userRepository The repository for user information used to handle user registration requests.
 */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
// ! For production, need to disable this.
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    /**
     * Handles a user registration request.
     *
     * @param request The registration request to be processed.
     * @return A response containing the authentication token for the newly registered user.
     * @throws InvalidEmailException If the email provided in the registration request is invalid.
     * @throws EmailAlreadyUsedException If the email provided in the registration request is already associated with an existing user.
     * @throws UnsecuredPasswordException If the password provided in the registration request is considered to be unsecure.
     * @throws InvalidUsernameException If the first or last name provided in the registration request is considered to be invalid.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request)
    {
        if(!request.getEmail().contains("@"))
            throw new InvalidEmailException();
        if(userRepository.existsByMail(request.getEmail()))
            throw new EmailAlreadyUsedException();

        if(request.getPassword().length() < 6)
            throw new UnsecuredPasswordException();
        if(InvalidUsernameException.BLACKLISTED_USERNAME_LIST.contains(request.getFirstName()) || InvalidUsernameException.BLACKLISTED_USERNAME_LIST.contains(request.getLastName()))
            throw new InvalidUsernameException(request.getFirstName()  + "||" + request.getLastName());
        return ResponseEntity.ok(authenticationService.register(request));

    }

    /**
     * Handles a user authentication request.
     *
     * @param request The authentication request to be processed.
     * @return A response containing the authentication token for the authenticated user.
     * @throws InvalidEmailException If the email provided in the authentication request is invalid.
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request)
    {
        if(!request.getEmail().contains("@"))
            ResponseEntity.badRequest();

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Handles a user authentication request using Google's SSO login.
     *
     * @param rawRequest The SSO login request to be processed.
     * @return A response containing the authentication token for the authenticated user.
     */
    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> authenticateWithGoogle(@RequestBody GoogleSSOLoginRequest rawRequest)
    {
        return ResponseEntity.ok(authenticationService.authenticateViaSSO(rawRequest));
    }

    /**
     * Handles a request to refresh a user's authentication token.
     *
     * @param request The token refresh request to be processed.
     * @return A response containing the new authentication token for the user.
     * @throws null If this method is called, as it has not yet been implemented.
     */

    @Profile("dev")
    @PostMapping("refresh")
    public ResponseEntity<String> refreshToken(@RequestBody TokenRefreshRequest request)
    {


        return null;
    }

}
