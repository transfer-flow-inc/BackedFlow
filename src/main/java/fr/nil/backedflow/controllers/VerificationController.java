package fr.nil.backedflow.controllers;


import fr.nil.backedflow.exceptions.InvalidTokenException;
import fr.nil.backedflow.reponses.AccountVerificationResponse;
import fr.nil.backedflow.requests.AccountVerificationRequest;
import fr.nil.backedflow.services.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verify")
public class VerificationController {

    private final UserVerificationService userVerificationService;

    @PostMapping()
    public ResponseEntity<AccountVerificationResponse> verifyUserAccount(@RequestBody(required = true) AccountVerificationRequest token) {
        if (token.getToken().isEmpty())
            throw new InvalidTokenException();

        if (userVerificationService.checkVerificationToken(token.getToken()))
            return ResponseEntity.ok(AccountVerificationResponse.builder().isAccountVerified(true).build());

        throw new InvalidTokenException();
    }

}
