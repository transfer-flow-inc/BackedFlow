package fr.nil.backedflow.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferNotificationEvent {

    private String senderName;
    private int fileCount;
    private float folderSize;
    private String folderMessage;
    private String downloadURL;
    private List<String> recipientsEmails;
}
