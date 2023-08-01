package fr.nil.backedflow.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.nil.backedflow.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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
    private Date uploaded_at;
    private Date expires_at;
    @ElementCollection
    private List<String> recipientsEmails;
    private String url;

    @JsonIgnore
    private String accessKey;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "_folder_file_entity_list",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "file_entity_list_id")
    )
    private List<FileEntity> fileEntityList = new ArrayList<>();;

    @ManyToOne
    private User folderOwner;
}
