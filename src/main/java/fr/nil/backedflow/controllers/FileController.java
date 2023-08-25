package fr.nil.backedflow.controllers;

import fr.nil.backedflow.services.folder.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {

    private final FolderService folderService;

    @PostMapping("/{folderID}")
    public ResponseEntity<?> singleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String folderID, HttpServletRequest request) {

        return ResponseEntity.ok(folderService.handleSingleFileUpload(file, UUID.fromString(folderID), request));

    }

}
