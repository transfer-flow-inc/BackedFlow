package fr.nil.backedflow.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {


    private String firstName;
    private String lastName;
    private String mail;
    private String password;


}
