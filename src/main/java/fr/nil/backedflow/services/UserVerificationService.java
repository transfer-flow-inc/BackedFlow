package fr.nil.backedflow.services;


import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserVerification;
import fr.nil.backedflow.exceptions.AccountAlreadyVerifiedException;
import fr.nil.backedflow.exceptions.InvalidTokenException;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserVerificationRepository;
import fr.nil.backedflow.services.utils.AccessKeyGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerificationService {

    private final UserVerificationRepository userVerificationRepository;
    private final UserRepository userRepository;

    public boolean checkVerificationToken(String verificationToken) {

        log.debug("Got a verification request with token : " + verificationToken);
        try {
            if (verificationToken.isEmpty())
                throw new InvalidTokenException();

            if (userVerificationRepository.findUserVerificationByVerificationToken(verificationToken).isEmpty())
                throw new InvalidTokenException();

            UserVerification userVerification = userVerificationRepository.findUserVerificationByVerificationToken(verificationToken).get();
            log.debug("User ID from UserVerificationEntity: " + userVerification.getUser().getId());
            if (userRepository.findUserById(userVerification.getUser().getId()).isEmpty())
                throw new EntityNotFoundException();

            User user = userRepository.findUserById(userVerification.getUser().getId()).get();


            if (Boolean.TRUE.equals(user.getIsAccountVerified()))
                throw new AccountAlreadyVerifiedException();

            verifyUser(user);

            userVerificationRepository.deleteById(userVerification.getId());
            log.debug("User account is now verified deleted the UserVerification Entity corresponding to this User");

            return true;
        } catch (AccountAlreadyVerifiedException | InvalidTokenException e) {
            log.debug("An error has occurred while checkVerificationToken stacktrace : " + Arrays.stream(e.getStackTrace()));
            return false;
        }

    }

    public void verifyUser(User user) {
        try {
            if (Boolean.TRUE.equals(user.getIsAccountVerified()))
                throw new AccountAlreadyVerifiedException();

            log.debug("Setting the user " + user.getId() + " account to verified.");
            user.setIsAccountVerified(true);
            user.setUserVerification(null);
            userRepository.save(user);
            log.debug("The user " + user.getId() + " has been verified.");

        } catch (AccountAlreadyVerifiedException e) {
            log.error("An error has occurred while setting the user account status to verified.");
            log.debug("An error has occurred while verifying the user account : " + Arrays.toString(e.getStackTrace()));
        }
    }

    public UserVerification generateVerificationToken(User user) {
        log.debug("Generating a new verification process for the user : " + user.getId());
        UserVerification userVerification = UserVerification.builder()
                .id(UUID.randomUUID())
                .user(user)
                .verificationToken(AccessKeyGenerator.generateVerificationToken())
                .build();

        log.debug("Saving the verification to the repository.");
        userVerification = userVerificationRepository.save(userVerification);


        user.setUserVerification(userVerification);
        userRepository.save(user);

        return userVerification;
    }


}
