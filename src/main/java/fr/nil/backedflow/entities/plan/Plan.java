package fr.nil.backedflow.entities.plan;

import fr.nil.backedflow.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_plan")
public class Plan {
    @Id
    @GeneratedValue()
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PlanType name;

    @OneToMany
    private List<User> users;

    private Float price;
    private Integer maxUploadCapacity;
    private String description;
    private Date startedAt;
    private Date endsAt;
}
