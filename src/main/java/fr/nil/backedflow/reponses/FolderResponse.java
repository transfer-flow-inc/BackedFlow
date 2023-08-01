package fr.nil.backedflow.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import fr.nil.backedflow.entities.Folder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {

    private Folder folder;
    private String accessKey;


}
