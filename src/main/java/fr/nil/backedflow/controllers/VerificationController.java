package fr.nil.backedflow.controllers;


import fr.nil.backedflow.exceptions.InvalidTokenException;
import fr.nil.backedflow.requests.AccountVerificationRequest;
import fr.nil.backedflow.responses.AccountVerificationResponse;
import fr.nil.backedflow.services.UserService;
import fr.nil.backedflow.services.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verify")
public class VerificationController {

    private final UserVerificationService userVerificationService;
    private final UserService userService;


    @PostMapping()
    public ResponseEntity<AccountVerificationResponse> verifyUserAccount(@RequestBody(required = true) AccountVerificationRequest token) {
        if (token.getToken().isEmpty())
            throw new InvalidTokenException();

        if (userVerificationService.checkAccountVerificationToken(token.getToken()))
            return ResponseEntity.ok(AccountVerificationResponse.builder().isAccountVerified(true).build());

        throw new InvalidTokenException();
    }

    @Transactional
    @DeleteMapping("/delete/{userID}/{deletionKey}")
    public void verifyUserAccountDeletion(@PathVariable UUID userID, @PathVariable String deletionKey) {
        if (deletionKey.isEmpty())
            throw new InvalidTokenException();
        if (userService.isDeletionKeyValid(userID, deletionKey)) {
            userService.deleteUserByID(userID);
        }
        throw new InvalidTokenException();
    }
}
