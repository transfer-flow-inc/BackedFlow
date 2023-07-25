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
 * This class represents a request to register a new user.
 *
 * @param firstName The first name of the user.
 * @param lastName The last name of the user.
 * @param email The email address of the user.
 * @param password The password of the user.
 */

public class RegisterRequest {

private String firstName;
private String lastName;
private String email;
private String password;


}
