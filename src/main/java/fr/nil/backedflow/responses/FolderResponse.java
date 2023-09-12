package fr.nil.backedflow.responses;

import fr.nil.backedflow.entities.Folder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {

    private Folder folder;
    private String accessKey;


}
