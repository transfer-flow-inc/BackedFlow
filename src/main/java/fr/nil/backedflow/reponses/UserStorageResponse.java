package fr.nil.backedflow.reponses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStorageResponse {

    private Float maxStorage;
    private Float usedStorage;

}