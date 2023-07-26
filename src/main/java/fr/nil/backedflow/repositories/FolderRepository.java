package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {





}