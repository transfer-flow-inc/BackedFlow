package fr.nil.backedflow.controllers;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.FolderNotFoundException;
import fr.nil.backedflow.exceptions.InvalidTokenException;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.requests.FolderCreationRequest;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.folder.FolderService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/folder")
@Slf4j
public class FolderController {

    private final FolderRepository folderRepository;
    private final FileService fileService;
    private final FolderService folderService;
    private final MeterRegistry meterRegistry;

    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolderFromID(@PathVariable(value = "id") String id) {
        if (folderRepository.findById(UUID.fromString(id)).isEmpty())
            throw new FolderNotFoundException("Can't find the request folder with the id : " + id);
        Folder folder = folderRepository.findById(UUID.fromString(id)).orElseThrow();
        folder.setAccessKey(null);
        return ResponseEntity.ok(folder);
    }


    @DeleteMapping("/{id}")
    public void deleteFolderFromID(@PathVariable(value = "id") String id, HttpServletRequest request) {

        folderService.handleDeleteFolder(id, request);

    }


    @GetMapping("/url/{folderURL}")
    public ResponseEntity<Folder> getFolderFromURL(@PathVariable(value = "folderURL") String folderURL, HttpServletRequest request) {
        return folderService.handleGetFolderURLRequest(folderURL, request);

    }
    @PostMapping("/")
    public ResponseEntity<Folder> createEmptyFolder(@RequestBody FolderCreationRequest folderCreationRequest, HttpServletRequest request) {

        return ResponseEntity.ok().body(folderService.createEmptyFolder(folderCreationRequest, request));

    }

    @PostMapping("/upload")
    public ResponseEntity<Folder> multipleFileUpload(@RequestParam("file") MultipartFile[] files, @PathVariable(required = false, name = "folderURL") String folderURL, HttpServletRequest request) {

        return folderService.handleMultipleFileUpload(files, folderURL, request);
    }

        // Continue with response creation...

    // Define a method to download files
    @SneakyThrows
    @GetMapping("/download/{folderURL}")
    public ResponseEntity<StreamingResponseBody> downloadFiles(@PathVariable("folderURL") String folderURL, @RequestParam("accessKey") String accessKey) {

        if (accessKey.isEmpty())
            throw new InvalidTokenException();
        if (!folderRepository.existsByUrl(folderURL))
            throw new FolderNotFoundException("get");
        Folder folder = folderRepository.getFolderByUrl(folderURL).orElseThrow();

        if (!accessKey.equals(folder.getAccessKey()))
            throw new InvalidTokenException();

        File zipFile = fileService.getZippedFiles(folder.getFileEntityList());


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
        InputStream fileInputStream = Files.newInputStream(zipFile.toPath());
        try {
            StreamingResponseBody stream = outputStream -> {
                int bytesRead;
                byte[] buffer = new byte[1024];
                while ((bytesRead = fileInputStream.read(buffer, 0, 1024)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            };

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(stream);


        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.internalServerError().build();
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
