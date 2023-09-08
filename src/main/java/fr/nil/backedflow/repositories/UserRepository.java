package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
    List<User> findAll();
    Optional<User> findUserById(UUID id);
    Optional<User> findByMail(String email);
    boolean existsByMail(String email);
    void deleteByMail(String email);
}

