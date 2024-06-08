package de.hsrm.quiz_gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileUploadController {
    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private Storage storage;

    private final String bucketName = "quizilla_media";

    @PostMapping("/uploadMedia")
    public ResponseEntity<String> handleImageUpload(@RequestParam("media") MultipartFile file) {
        logger.info("Received request to upload media");
        return uploadFileToGCS(file);
    }

    private ResponseEntity<String> uploadFileToGCS(MultipartFile file) {
        try {
            logger.info("Uploading media: " + file.getOriginalFilename());
            BlobId blobId = BlobId.of(bucketName, file.getOriginalFilename());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload media: " + e.getMessage());
        }
    }

    @GetMapping("/downloadMedia/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFileFromGCS(@PathVariable String fileName) {
        logger.info("Received request to download media: " + fileName);
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        if (blob == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(blob.getContent());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(blob.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

}
