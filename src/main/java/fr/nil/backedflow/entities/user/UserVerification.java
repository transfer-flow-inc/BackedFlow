package fr.nil.backedflow.entities.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user_verification")

public class UserVerification {

    @Id
    public UUID id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    public User user;

    public String verificationToken;


}
