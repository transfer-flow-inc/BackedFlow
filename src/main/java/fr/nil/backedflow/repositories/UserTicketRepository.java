package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.user.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTicketRepository extends JpaRepository<UserTicket, Integer> {

}
