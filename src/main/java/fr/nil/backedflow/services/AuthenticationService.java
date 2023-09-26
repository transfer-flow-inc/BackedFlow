    package fr.nil.backedflow.services;


    import fr.nil.backedflow.auth.exceptions.InvalidSSOLoginRequest;
    import fr.nil.backedflow.auth.requests.AuthenticationRequest;
    import fr.nil.backedflow.auth.requests.GoogleSSOLoginRequest;
    import fr.nil.backedflow.auth.requests.RegisterRequest;
    import fr.nil.backedflow.auth.responses.AuthenticationResponse;
    import fr.nil.backedflow.entities.plan.PlanType;
    import fr.nil.backedflow.entities.user.Role;
    import fr.nil.backedflow.entities.user.User;
    import fr.nil.backedflow.entities.user.UserVerification;
    import fr.nil.backedflow.event.AccountCreationEvent;
    import fr.nil.backedflow.exceptions.UnverifiedLoginException;
    import fr.nil.backedflow.repositories.PlanRepository;
    import fr.nil.backedflow.repositories.UserRepository;
    import io.micrometer.core.instrument.MeterRegistry;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.core.env.Environment;
    import org.springframework.core.env.Profiles;
    import org.springframework.kafka.core.KafkaTemplate;
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
@Slf4j
public class AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserVerificationService userVerificationService;
    private final PlanRepository planRepository;
    private final MeterRegistry meterRegistry;
        private final MeterService meterService;

        private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${transferflow.auth.sso.google.client.id:979451349689-s05pddc23jr0m0769u04ir93vj5t9mp0.apps.googleusercontent.com}")
    private String googleSSOClientID;
        private final Environment env;

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
                .plan(planRepository.save(PlanType.FREE.toPlan()))
                .isAccountVerified(false)
                .avatar("https://api.transfer-flow.studio/assets/logo_dark.png")
                .build();

        if (env.acceptsProfiles(Profiles.of("apitesting")))
            user.setIsAccountVerified(true);

        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(generateExtraClaims(user, "spring_database"), user);
        meterService.incrementUserRegistrationCounter();
        UserVerification userVerification = userVerificationService.generateVerificationToken(user);

        kafkaTemplate.send("accountCreationTopic", AccountCreationEvent.builder()
                        .userID(user.getId().toString())
                        .userName(user.getFirstName() + " " + user.getLastName())
                        .email(user.getMail())
                .validationToken(userVerification.getVerificationToken())
                .build());

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

        if (!user.getIsAccountVerified())
            throw new UnverifiedLoginException();

        meterService.incrementSpringLoginCounter();
        String jwtToken = jwtService.generateToken(generateExtraClaims(user, "spring_database"), user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

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
        if (!Objects.equals(request.getAzp(), googleSSOClientID)) {
            log.debug("The azp is not equal to the stored ClientID");
            throw new InvalidSSOLoginRequest();
        }
        if (!Objects.equals(request.getAzp(), request.getAud())) {
            log.debug("Mismatch in azp and aud");
            throw new InvalidSSOLoginRequest();
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .mail(request.getEmail())
                .password(passwordEncoder.encode(request.getJti()))
                .isAccountVerified(true)
                .role(Role.USER)
                .avatar(request.getPicture())
                .plan(planRepository.save(PlanType.FREE.toPlan()))
                .build();

        if (!userRepository.existsByMail(request.getEmail()))
            user = userRepository.save(user);
        else
            userRepository.updateUser(user.getFirstName(), user.getLastName(), user.getMail(), user.getPassword(), userRepository.findByMail(user.getMail()).get().getId(), user.getAvatar());

        meterService.incrementGoogleSSOLoginCounter();
        String jwtToken = jwtService.generateToken(generateExtraClaims(user, "google_sso"), user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }


        private Map<String, Object> generateExtraClaims(User user, String authMethod) {
        Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("authMethod", authMethod);
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("userEmail", user.getMail());
        extraClaims.put("userRole", user.getRole());
            extraClaims.put("userID", userRepository.existsByMail(user.getMail()) ? userRepository.findByMail(user.getMail()).orElseThrow().getId() : userRepository.save(user).getId());
        extraClaims.put("isAccountVerified", user.getIsAccountVerified());
        extraClaims.put("plan", user.getPlan().getName());
        extraClaims.put("avatar", user.getAvatar());

            return extraClaims;
    }

}
