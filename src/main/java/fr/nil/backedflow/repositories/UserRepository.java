package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {


    Optional<User> findByMail(String email);
    boolean existsByMail(String email);
    void deleteByMail(String email);
}

