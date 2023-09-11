package fr.nil.backedflow.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime uploadedAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm")
    private LocalDateTime expiresAt;
    private Long fileSize;
    private String fileType;
    private String filePath;
    private String fileName;
    private boolean isArchive;

    @JsonIgnore
    @ManyToOne
    private Folder folder;

}

