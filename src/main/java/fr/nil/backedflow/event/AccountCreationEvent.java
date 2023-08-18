package fr.nil.backedflow.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreationEvent {

    public String userID;

    //temp variable
    public String userName;

    public String email;
    public String validationToken;
}
