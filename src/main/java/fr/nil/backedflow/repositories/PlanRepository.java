package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {


}
