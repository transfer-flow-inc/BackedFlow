package fr.nil.backedflow.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import fr.nil.backedflow.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_folder")
public class Folder {

    @Id
    @GeneratedValue()
    private UUID id;

    private String folderName;

    private Long folderSize;

    private int fileCount;

    private boolean isPrivate;

    private boolean isShared;

    private int folderViews;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime uploadedAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime expiresAt;

    @ElementCollection
    private List<String> recipientsEmails;

    private String url;

    private String message;

    private String accessKey;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "_folder_file_entity_list",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "file_entity_list_id")
    )
    private List<FileEntity> fileEntityList = new ArrayList<>();

    @ManyToOne()
    private User folderOwner;
}
