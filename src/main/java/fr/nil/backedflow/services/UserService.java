package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.repositories.UserRepository;
import fr.nil.backedflow.repositories.UserVerificationRepository;
import jakarta.persistence.EntityManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserVerificationRepository userVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("UserID: " + id));
    }
    public User getUserByMail(String mail) {
        return userRepository.findByMail(mail).orElseThrow(() -> new UsernameNotFoundException(mail));
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public AuthenticationResponse updateUser(String mail, UserUpdateRequest updateRequest, String oldPassword) throws PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findByMail(mail);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException(mail);
        }

        User user = optionalUser.get();
        log.warn("Yo wtf : " + oldPassword + " upass " + user.getPassword() + " doespassmatch ? " + passwordEncoder.matches(oldPassword, user.getPassword()));

        if (oldPassword != null && !passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new PasswordMismatchException();


        if (updateRequest.getFirstName() != null) {
            log.debug("firstName will be updated from this request");
            user.setFirstName(updateRequest.getFirstName());
                    }

        if (updateRequest.getLastName() != null) {
            log.debug("lastName will be updated from this request");
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getMail() != null) {
            log.debug("mail will be updated from this request ");
            user.setMail(updateRequest.getMail());
                    }

        if (updateRequest.getPassword() != null) {
            log.debug("Password will be updated from this request");
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
                    }

        userRepository.save(user);


        if (oldPassword != null && !passwordEncoder.matches(oldPassword, user.getPassword()))
            return authenticationService.authenticate(AuthenticationRequest.builder().email(mail).password(updateRequest.getPassword()).build());

        return authenticationService.authenticate(AuthenticationRequest.builder().email(mail).password(oldPassword).build());
    }


    public void deleteUserByEmail(String email) {
        userVerificationRepository.deleteByUserMail(email);
        entityManager.flush();
        userRepository.deleteByMail(email);
    }



}
