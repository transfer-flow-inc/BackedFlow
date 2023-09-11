package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserTicket;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserTicketRepository;
import fr.nil.backedflow.requests.TicketMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTicketServiceTest {

    @Mock
    private UserTicketRepository userTicketRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserTicketService userTicketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleTicketRequest() {
        // Given
        TicketMessageRequest ticketRequest = new TicketMessageRequest("test@mail.com", "subject", "message");
        User user = new User();
        user.setMail("test@mail.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findByMail(ticketRequest.getUserEmail())).thenReturn(Optional.of(user));

        UserTicket expectedUserTicket = UserTicket.builder()
                .user(user)
                .subject(ticketRequest.getSubject())
                .message(ticketRequest.getMessage())
                .sentAt(now)
                .build();

        when(userTicketRepository.save(any(UserTicket.class))).thenReturn(expectedUserTicket);

        // When
        UserTicket actualUserTicket = userTicketService.handleTicketRequest(ticketRequest);

        // Then
        verify(userRepository).findByMail(ticketRequest.getUserEmail());
        verify(userTicketRepository, times(2)).save(any(UserTicket.class));
        assertEquals(expectedUserTicket, actualUserTicket);
    }
}
