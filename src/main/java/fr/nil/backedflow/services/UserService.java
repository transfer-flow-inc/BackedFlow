package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.InvalidRequestException;
import fr.nil.backedflow.repositories.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

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


    public AuthenticationResponse updateUserByEmail(String email, String oldPassword, User updatedUser) {

        return userRepository.findByMail(email)
                .map(existingUser -> {
                    if (!passwordEncoder.matches(oldPassword, existingUser.getPassword()))
                        throw new InvalidRequestException("updateUserByEmail, the password is incorrect.");

                    if (updatedUser.getFirstName() != null) {
                        existingUser.setFirstName(updatedUser.getFirstName());
                    }
                    if (updatedUser.getLastName() != null) {
                        existingUser.setLastName(updatedUser.getLastName());
                    }
                    if (updatedUser.getMail() != null) {
                        existingUser.setMail(updatedUser.getMail());
                    }
                    if (updatedUser.getPassword() != null || !updatedUser.getPassword().equals("null")) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    userRepository.save(existingUser);
                    if (passwordEncoder.matches(oldPassword, existingUser.getPassword()))
                        return authenticationService.authenticate(AuthenticationRequest.builder().email(email).password(updatedUser.getPassword()).build());

                    return authenticationService.authenticate(AuthenticationRequest.builder().email(email).password(oldPassword).build());
                })
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
/*
    public User updateUserByEmail(String email, User updatedUser) {

        return userRepository.findByMail(email)
                .map(existingUser -> {
                    if (updatedUser.getFirstName() != null) {
                        existingUser.setFirstName(updatedUser.getFirstName());
                    }
                    if (updatedUser.getLastName() != null) {
                        existingUser.setLastName(updatedUser.getLastName());
                    }
                    if (updatedUser.getMail() != null) {
                        existingUser.setMail(updatedUser.getMail());
                    }
                    if (updatedUser.getPassword() != null) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
    *
 */
    public void deleteUserByEmail(String email) {

        userRepository.deleteByMail(email);
    }



}
