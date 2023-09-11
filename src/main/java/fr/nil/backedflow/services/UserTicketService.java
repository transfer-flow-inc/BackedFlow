package fr.nil.backedflow.services;

import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserTicket;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserTicketRepository;
import fr.nil.backedflow.requests.TicketMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTicketService {
    private final UserTicketRepository userTicketRepository;
    private final UserRepository userRepository;


    public UserTicket handleTicketRequest(TicketMessageRequest ticketRequest) {

        User user = userRepository.findByMail(ticketRequest.getUserEmail()).orElseThrow();
        UserTicket userTicket = UserTicket.builder()
                .user(user)
                .subject(ticketRequest.getSubject())
                .message(ticketRequest.getMessage())
                .build();
        userTicketRepository.save(userTicket);
        return userTicketRepository.save(userTicket);
    }

}
