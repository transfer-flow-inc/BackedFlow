package fr.nil.backedflow.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderCreationRequest {

    private String folderName;
    private Long folderSize;
    private int fileCount;
    private List<String> recipientsEmails;
    private String message;
}
