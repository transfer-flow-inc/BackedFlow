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
@Table(name = "_plan")
public class Plan {
    @Id
    @GeneratedValue()
    private UUID id;
    private String name;
    private int price;
    private int maxUploadCapacity;
    private String description;
    private Date started_at;
    private Date ends_at;
}
