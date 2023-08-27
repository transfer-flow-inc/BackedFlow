package fr.nil.backedflow.controllers;

import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.FolderNotFoundException;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.requests.FolderCreationRequest;
import fr.nil.backedflow.services.files.FileService;
import fr.nil.backedflow.services.folder.FolderService;
import fr.nil.backedflow.stats.MetricsEnum;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/folder")
public class FolderController {

    private final FolderRepository folderRepository;
    private final FileService fileService;
    private final FolderService folderService;
    private final MeterRegistry meterRegistry;

    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolderFromID(@PathVariable(value = "id") String id) {
        if (folderRepository.findById(UUID.fromString(id)).isEmpty())
            throw new FolderNotFoundException("get");

        Folder folder = folderRepository.findById(UUID.fromString(id)).get();

        return ResponseEntity.ok(folder);
    }

    // todo Get folder by URL for front-end

    @PostMapping("/")
    public ResponseEntity<Folder> createEmptyFolder(@RequestBody FolderCreationRequest folderCreationRequest, HttpServletRequest request) {

        return ResponseEntity.ok().body(folderService.createEmptyFolder(folderCreationRequest, request));

    }

    @PostMapping("/upload")
    public ResponseEntity<?> multipleFileUpload(@RequestParam("file") MultipartFile[] files, @PathVariable(required = false, name = "folderURL") String folderURL, HttpServletRequest request) {

        return folderService.handleMultipleFileUpload(files, folderURL, request);
    }

        // Continue with response creation...

    // Define a method to download files
    @GetMapping("/download/{folderURL}")
    public ResponseEntity<?> downloadFiles(@PathVariable("folderURL") String folderURL, @RequestParam("accessKey") String accessKey) throws IOException{
        if(accessKey.isEmpty())
            return new ResponseEntity<>("Invalid access key!", HttpStatus.FORBIDDEN);
        if(!folderRepository.existsByUrl(folderURL))
            throw new FolderNotFoundException("get");
        Folder folder = folderRepository.getFolderByUrl(folderURL).get();

        if(!accessKey.equals(folder.getAccessKey()))
            return new ResponseEntity<>("Invalid access key!", HttpStatus.FORBIDDEN);

        File zipFile = fileService.getZippedFiles(folder.getFileEntityList());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
        meterRegistry.counter(MetricsEnum.FILE_TRANSFER_DOWNLOAD_COUNT.getMetricName()).increment();
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(zipFile));
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
