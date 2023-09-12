package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.repositories.PlanRepository;
import fr.nil.backedflow.repositories.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

class AuthenticationServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserVerificationService userVerificationService;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private Environment env;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(UUID.randomUUID()).firstName("test").lastName("test").build();

    }


}
