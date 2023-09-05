package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.repositories.*;
import fr.nil.backedflow.services.files.FileService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PlanRepository planRepository;
    private final FileEntityRepository fileEntityRepository;
    private final FolderRepository folderRepository;
    private final UserVerificationRepository userVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final JWTService jwtService;

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

    public UUID getUserIDFromRequest(HttpServletRequest request) {
        return UUID.fromString(jwtService.extractClaim(request.getHeader("Authorization").replace("Bearer", ""), claims -> claims.get("userID").toString()));
    }


    public AuthenticationResponse updateUser(String mail, UserUpdateRequest updateRequest, String oldPassword) throws PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findByMail(mail);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException(mail);
        }
        User user = optionalUser.get();

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

        User user = userRepository.findByMail(email).orElseThrow();

        if (folderRepository.findAllByFolderOwner(user.getId()).isPresent()) {
            if (log.isDebugEnabled())
                log.debug(String.format("Deleting all files in folders for the user %s", user.getId()));

            folderRepository.findAllByFolderOwner(user.getId()).get().forEach(fileService::deleteFilesFromUserStorage);
            folderRepository.deleteAllByFolderOwnerMail(email);
        }

        entityManager.flush();
        planRepository.delete(user.getPlan());
        userRepository.deleteByMail(email);
        userVerificationRepository.deleteByUserMail(email);

    }



}
