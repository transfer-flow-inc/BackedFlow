package fr.nil.backedflow.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletionNotificationEvent {

    private UUID userID;
    private String userName;
    private String mail;
    private String validationToken;

}