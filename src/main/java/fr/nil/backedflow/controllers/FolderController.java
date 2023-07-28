package fr.nil.backedflow.controllers;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.FolderNotFoundException;
import fr.nil.backedflow.repositories.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/folder")
public class FolderController {

    private final FolderRepository folderRepository;



    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolderFromID(@PathVariable(value = "id") String id)
    {
        if (!folderRepository.findById(UUID.fromString(id)).isPresent())
            throw new FolderNotFoundException("get");

        Folder folder = folderRepository.findById(UUID.fromString(id)).get();

        return ResponseEntity.ok(folder);
    }

    @Profile("testing")
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/v1/test/")
    public class RandomEndpointController {

        @Autowired
        private FolderRepository folderRepository;

        @GetMapping("/folder/random")
        public ResponseEntity<Folder> getRandomFolder() {
            // Retrieve a random folder entity from the database
            Optional<Folder> randomFolder = folderRepository.getRandomFolder();

            return randomFolder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
}
