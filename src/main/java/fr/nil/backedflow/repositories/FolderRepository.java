package fr.nil.backedflow.repositories;

import fr.nil.backedflow.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface FolderRepository extends JpaRepository<Folder, UUID> {

        @Query("SELECT f from Folder f WHERE f.folderOwner.id = :userId")
        Optional<List<Folder>> findAllByFolderOwner(@Param("userId") UUID userId);

        Optional<Folder> getFolderByUrl(@Param("url") String url);

        @Query(value = "SELECT f FROM Folder f ORDER BY RAND() LIMIT 1")
        Optional<Folder> getRandomFolder();

        boolean existsByUrl(@Param("url") String url);

}