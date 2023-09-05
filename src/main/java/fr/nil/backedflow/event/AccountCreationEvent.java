package fr.nil.backedflow.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreationEvent {

    private String userID;

    //temp variable
    private String userName;

    private String email;
    private String validationToken;
}
