package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
public interface FolderRepository extends JpaRepository<Folder, UUID> {

        List<Folder> findAllByUserId(@Param("userId") UUID userId);

}