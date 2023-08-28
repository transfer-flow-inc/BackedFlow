package fr.nil.backedflow.controllers;


import fr.nil.backedflow.auth.requests.UserUpdateRequest;
import fr.nil.backedflow.auth.responses.AuthenticationResponse;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.InvalidRequestException;
import fr.nil.backedflow.exceptions.PasswordMismatchException;
import fr.nil.backedflow.services.UserService;
import fr.nil.backedflow.services.folder.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final FolderService folderService;
    @PatchMapping("/{email}")
    public ResponseEntity<AuthenticationResponse> updateUserById(@PathVariable(value = "email", required = true) String email, @RequestParam(value = "oldPassword", required = false) String oldPassword, @RequestBody UserUpdateRequest userUpdateRequest, Authentication authentication) throws PasswordMismatchException {
        String userEmail = authentication.getName();
        logger.debug("An update request for the user " + email + " has been requested by " + userEmail);


        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
            return ResponseEntity.ok(userService.updateUser(email, userUpdateRequest, oldPassword));

        if (!userEmail.equals(email))
            throw new InvalidRequestException("You are not authorized to delete this account");

        return ResponseEntity.ok(userService.updateUser(email, userUpdateRequest, oldPassword));
    }

    @GetMapping("/folders/{userID}")
    public ResponseEntity<List<Folder>> getAllFoldersByUserID(@PathVariable(value = "userID") String userID, HttpServletRequest request) {

        return folderService.getAllFolderByUserID(userID, request);
    }
    @DeleteMapping("/{email}")
    public void deleteUserByEmail(@PathVariable(value = "email", required = true) String email, Authentication authentication) {
        String userEmail = authentication.getName();
        logger.debug("User with the email : " + userEmail + ", has requested the account deletion of the account : " + email);
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
            userService.deleteUserByEmail(email);

        if (!userEmail.equals(email))
            throw new InvalidRequestException("You are not authorized to delete this account");

        userService.deleteUserByEmail(email);
        logger.debug("The account with the email : " + email + " has been deleted.");
    }


}


