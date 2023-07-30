package fr.nil.backedflow.controllers;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.AccessKeyException;
import fr.nil.backedflow.exceptions.FolderNotFoundException;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.services.JWTService;
import fr.nil.backedflow.services.folder.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/folder")
public class FolderController {

    private final FolderRepository folderRepository;
    private final FolderService folderService;

    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolderFromID(@PathVariable(value = "id") String id) {
        if (!folderRepository.findById(UUID.fromString(id)).isPresent())
            throw new FolderNotFoundException("get");

        Folder folder = folderRepository.findById(UUID.fromString(id)).get();

        return ResponseEntity.ok(folder);
    }


    @PostMapping("/upload")
    public ResponseEntity<?> multipleFileUpload(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {

        return folderService.handleMultipleFileUpload(files,request);
    }

        // Continue with response creation...

    // Define a method to download files
    @GetMapping("/download/{folderURL}")
    public ResponseEntity<?> downloadFiles(@PathVariable("folderURL") String folderURL, @RequestParam("accessKey") String accessKey) throws IOException, MalformedURLException, AccessKeyException {
        if(accessKey.isEmpty())
            return new ResponseEntity<>("Invalid access key!", HttpStatus.FORBIDDEN);

        Folder folder = folderRepository.getFolderByUrl(folderURL).get();

        if(!accessKey.equals(folder.getAccessKey()))
            return new ResponseEntity<>("Invalid access key!", HttpStatus.FORBIDDEN);


        for(FileEntity file : folder.getFileEntityList()) {
            Resource resource = new UrlResource("file:" + file.getFilePath());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("File-Name", file.getFileName());
            httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());
            return ResponseEntity.ok().contentType(MediaType.MULTIPART_FORM_DATA)
                    .headers(httpHeaders).body(resource);
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(Path.of(folder.getFileEntityList().get(1).getFilePath()))))
                .body(new UrlResource(folder.getFileEntityList().get(1).getFileName()));
    }



    @Profile("testing")
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/v1/test/")
    public static class RandomEndpointController {

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
