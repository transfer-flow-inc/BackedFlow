package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FileEntityRepository extends JpaRepository<FileEntity, UUID> {

    List<FileEntity> findFileEntitiesByFolderId(@Param("folderId") UUID folderID);

}
