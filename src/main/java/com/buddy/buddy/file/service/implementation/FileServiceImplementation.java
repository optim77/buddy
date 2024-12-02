package com.buddy.buddy.file.service.implementation;

import com.buddy.buddy.file.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImplementation implements FileService {

    @Value("${app.file.storage-path}")
    private String storagePath;

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImplementation.class.getName());

    @Override
    public ResponseEntity<Resource> getFile(String fileName, String cookie) {
        try {
            if (!hasAccessToFile(cookie, fileName)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Path filePath = Paths.get(storagePath).resolve(fileName);
            Resource fileResource = new UrlResource(filePath.toUri());

            if (!fileResource.exists() || !fileResource.isReadable()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // TODO: extends types
            String contentType = "image/jpeg";

            if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".gif")) {
                contentType = "image/gif";
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(fileResource);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasAccessToFile(String cookie, String fileName) {
        // TODO: Implement logic for checking if the user has access (e.g., based on cookie or user role)
        return true;
    }
}
