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
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue()
    private UUID id;
    private String firstName;
    private String  lastName;
    private String mail;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String avatar;
    private boolean isAccountVerified;


}
