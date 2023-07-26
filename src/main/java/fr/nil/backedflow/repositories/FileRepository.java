package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {

    //@Query("SELECT f FROM File f WHERE f.folder.id = :folderId")
    List<File> findAllByFolderId(@Param("folderId") UUID folderId);

}
