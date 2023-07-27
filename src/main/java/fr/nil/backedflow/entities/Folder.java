package fr.nil.backedflow.entities;

import fr.nil.backedflow.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private Date uploaded_at;
    private Date expires_at;
    @ElementCollection
    private List<String> recipientsEmails;
    private String url;

    @OneToMany(mappedBy="folder")
    private List<FileEntity> fileEntityList;

    @ManyToOne
    private User folderOwner;
}
