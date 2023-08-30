package fr.nil.backedflow.controllers;


import fr.nil.backedflow.reponses.AccountVerificationResponse;
import fr.nil.backedflow.services.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verify")
public class VerificationController {

    private final UserVerificationService userVerificationService;

    @GetMapping()
    public ResponseEntity<?> verifyUserAccount(@RequestParam(value = "token") String token) {
        if (token.isEmpty())
            return ResponseEntity.badRequest().body(AccountVerificationResponse.builder().isAccountVerified(false).build());

        if (userVerificationService.checkVerificationToken(token))
            return ResponseEntity.ok(AccountVerificationResponse.builder().isAccountVerified(true));

        return ResponseEntity.internalServerError().body("Something went wrong during the account verification (token: " + token + ")");
    }

}
