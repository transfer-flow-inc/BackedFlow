package fr.nil.backedflow.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

/**
 This class represents a request to authenticate a user and retrieve a token.
 @param email The email address of the user.

 @param password The password of the user.
 */
public class AuthenticationRequest {

    private String email;
    private String password;

}
