package fr.nil.backedflow.services;


import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.entities.user.User;
import fr.nil.backedflow.event.AccountCreationEvent;
import fr.nil.backedflow.event.DeletionNotificationEvent;
import fr.nil.backedflow.event.TransferNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendAccountCreationEvent(User user, String validationToken) {
        kafkaTemplate.send("accountCreationTopic", AccountCreationEvent.builder()
                .userID(user.getId().toString())
                .userName(user.getFirstName() + " " + user.getLastName())
                .email(user.getMail())
                .validationToken(validationToken)
                .build());
    }

    public void sendTransferNotification(User user, Folder folder) {
        kafkaTemplate.send("transferNotificationTopic", TransferNotificationEvent.builder()
                .senderName(user.getFirstName() + " " + user.getLastName())
                .folderMessage(folder.getMessage())
                .folderSize(folder.getFolderSize())
                .downloadURL("https://transfer-flow.studio/telechargement/" + folder.getUrl() + "/" + folder.getAccessKey())
                .fileCount(folder.getFileCount())
                .folderName(folder.getFolderName())
                .folderMessage(!folder.getMessage().isEmpty() ? folder.getMessage() : "Pas de message joint au transfer")
                .recipientsEmails(folder.getRecipientsEmails())
                .build());
    }


    public void sendDeletionNotificationEvent(User user) {
        kafkaTemplate.send("deletionNotificationTopic", DeletionNotificationEvent.builder()
                .userID(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .mail(user.getMail())
                .validationToken(user.getDeletionKey())
                .build());
    }


}
