package fr.nil.backedflow.services;

import fr.nil.backedflow.auth.requests.AuthenticationRequest;
import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.entities.user.UserVerification;
import fr.nil.backedflow.event.AccountCreationEvent;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.manager.StorageManager;
import fr.nil.backedflow.repositories.*;
import fr.nil.backedflow.responses.UserStorageResponse;
import fr.nil.backedflow.services.files.FileService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    private final StorageManager storageManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserVerificationService userVerificationService;



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


    public boolean canUserUpload(User user) {
        return user.getIsAccountVerified() && storageManager.hasEnoughStorageSize(user);
    }


    public AuthenticationResponse updateUser(String mail, UserUpdateRequest updateRequest, String oldPassword) throws PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findByMail(mail);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(mail);
        }
        User user = optionalUser.get();

        if (oldPassword.isEmpty())
            throw new PasswordMismatchException("Old password is empty");

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new PasswordMismatchException("Old password does not match user password ");


        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
            log.debug("firstName will be updated from this request");
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
            log.debug("lastName will be updated from this request");
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty() && !updateRequest.getEmail().equals(mail)) {
            log.debug("mail will be updated from this request ");
            user.setMail(updateRequest.getEmail());
            user.setIsAccountVerified(false);

            UserVerification userVerification = userVerificationService.generateVerificationToken(user);

            kafkaTemplate.send("accountCreationTopic", AccountCreationEvent.builder()
                    .userID(user.getId().toString())
                    .userName(user.getFirstName() + " " + user.getLastName())
                    .email(updateRequest.getEmail())
                    .validationToken(userVerification.getVerificationToken())
                    .build());
            log.debug(String.format("User mail has been updated, a new verification process has been started %s", mail));

            mail = updateRequest.getEmail();
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            log.debug("Password will be updated from this request");
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        userRepository.save(user);

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            return authenticationService.authenticate(AuthenticationRequest.builder().email(mail).password(updateRequest.getPassword()).build());

        return authenticationService.authenticate(AuthenticationRequest.builder().email(mail).password(oldPassword).build());
    }


    public void deleteUserByEmail(String email) {

        User user = getUserByMail(email);

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


    public UserStorageResponse getUserStorageInfo(String userID) {
        User user = getUserById(UUID.fromString(userID));

        return UserStorageResponse.builder()
                .usedStorage(storageManager.checkUserStorageSize(user))
                .maxStorage(user.getPlan().getMaxUploadCapacity().longValue() * 1073741824)
                .build();


    }

    public float getUserStorageUsagePercentage(String userID) {
        User user = getUserById(UUID.fromString(userID));

        float usedStorage = storageManager.getFormattedUserStorageSize(user);
        float maxStorage = user.getPlan().getMaxUploadCapacity();

        return (usedStorage / maxStorage) * 100; // Calculate the percentage
    }


    public User addFolderToFolderList(User user, Folder folder) {
        user.getUserFolders().add(folder);
        return userRepository.save(user);
    }

}
