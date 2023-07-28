package fr.nil.backedflow.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_file")
public class FileEntity {

    @Id
    @GeneratedValue()
    private UUID id;
    private Date uploadedAt;
    private Date expiresAt;
    private Long fileSize;
    private String fileType;
    private String filePath;
    private String fileName;
    private boolean isArchive;

    @JsonIgnore
    @ManyToOne
    private Folder folder;

}

