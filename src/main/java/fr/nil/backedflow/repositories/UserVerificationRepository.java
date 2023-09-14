package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.user.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {


    Optional<UserVerification> findUserVerificationByVerificationToken(String verificationToken);

    Optional<UserVerification> findUserVerificationByUserId(UUID id);
    void deleteByUserMail(String mail);
}
