package fr.nil.backedflow.controllers;

import fr.nil.backedflow.entities.FileEntity;
import fr.nil.backedflow.entities.Folder;
import fr.nil.backedflow.exceptions.FolderNotFoundException;
import fr.nil.backedflow.exceptions.InvalidTokenException;
import fr.nil.backedflow.repositories.FolderRepository;
import fr.nil.backedflow.requests.FolderCreationRequest;
import fr.nil.backedflow.services.MeterService;
import fr.nil.backedflow.services.files.FileEncryptorDecryptor;
import fr.nil.backedflow.services.folder.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/folder")
@Slf4j
public class FolderController {

    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final MeterService meterService;
    private final FileEncryptorDecryptor fileEncryptorDecryptor;

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
    public ResponseEntity<Folder> getFolderFromURL(@PathVariable(value = "folderURL") String folderURL) {
        return folderService.handleGetFolderURLRequest(folderURL);

    }
    @PostMapping("/")
    public ResponseEntity<Folder> createEmptyFolder(@RequestBody FolderCreationRequest folderCreationRequest, HttpServletRequest request) {

        return ResponseEntity.ok().body(folderService.createEmptyFolder(folderCreationRequest, request));

    }

    @GetMapping("/download/{folderURL}")
    public void downloadFilesAsync(HttpServletResponse response, @PathVariable("folderURL") String folderURL, @RequestParam("accessKey") String accessKey) throws IOException {
        if (accessKey.isEmpty())
            throw new InvalidTokenException();
        if (!folderRepository.existsByUrl(folderURL))
            throw new FolderNotFoundException("get");
        Folder folder = folderRepository.getFolderByUrl(folderURL).orElseThrow();

        if (!accessKey.equals(folder.getAccessKey()))
            throw new InvalidTokenException();

        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"");

        ExecutorService executorService = Executors.newFixedThreadPool(16); // Create a thread pool
        List<Future<File>> futures = new ArrayList<>();

        for (FileEntity fileEntity : folder.getFileEntityList()) {
            File originalFile = new File(fileEntity.getFilePath());
            Future<File> future = executorService.submit(() -> fileEncryptorDecryptor.getDecryptedFile(originalFile)); // Decrypt files concurrently
            futures.add(future);
        }

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; i < folder.getFileEntityList().size(); i++) {
                FileEntity fileEntity = folder.getFileEntityList().get(i);
                File decryptedFile = futures.get(i).get(); // Wait for the decryption to finish

                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setFileNameInZip(fileEntity.getFileName());
                zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);

                // Add new zip entry and copy the file into the zip output stream
                try (FileInputStream fis = new FileInputStream(decryptedFile)) {
                    zipOut.putNextEntry(zipParameters);
                    IOUtils.copy(fis, zipOut);
                    zipOut.closeEntry();
                }
            }
            meterService.incrementFileDownloadCounter();
            meterService.updateDownloadFileSizeGauge(folder.getFolderSize());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            executorService.shutdown(); // Don't forget to shut down the executor
        }
    }

}
