package fr.nil.backedflow.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
public class File {

    @Id
    @GeneratedValue()
    private UUID id;
    private Date uploadedAt;
    private Date expiresAt;
    private Byte fileSize;
    private String fileType;
    private String filePath;
    private String fileName;
    private boolean isArchive;



}
