package com.buddy.buddy.file.service.implementation;

import com.buddy.buddy.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileServiceImplementation implements FileService {

    @Value("${app.file.storage-path}")
    private String storagePath;

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImplementation.class.getName());

    @Override
    public ResponseEntity<Resource> getFile(String fileName, String cookie) {
        try {
            logger.debug("Getting file: " + fileName);
            if (!hasAccessToFile(cookie, fileName)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            HttpServletRequest request =
                    ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                            .getRequest();

            Path filePath = Paths.get(storagePath).resolve(fileName);
            File file = filePath.toFile();
            String eTag  = "\"" + Integer.toHexString(fileName.hashCode()) + "\"";

            if (!file.exists() || !file.canRead()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            long fileLength = file.length();
            String contentType = Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // TODO: extends types
//            String contentType = "image/jpeg";
//
//            if (fileName.endsWith(".png")) {
//                contentType = "image/png";
//            } else if (fileName.endsWith(".gif")) {
//                contentType = "image/gif";
//            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept-Ranges", "bytes");
            headers.add("Cache-Control", "public, max-age=604800");
            headers.setETag(eTag);
            headers.setLastModified(file.lastModified());

            String range = request.getHeader("Range");
            if (range != null && range.startsWith("bytes=")) {
                logger.debug("Split bytes in range: " + range);
                String[] parts = range.replace("bytes=", "").split("-");
                long start = Long.parseLong(parts[0]);
                long end = parts.length > 1 && !parts[1].isEmpty() ? Long.parseLong(parts[1]) : fileLength - 1;
                if (end >= fileLength) end = fileLength - 1;

                long contentLength = end - start + 1;
                InputStream inputStream = new FileInputStream(file);
                inputStream.skip(start);

                headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                headers.setContentLength(contentLength);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(MediaType.parseMediaType(contentType))
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));

            }

            logger.debug("Return whole file: " + fileName);
            headers.setContentLength(fileLength);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .body(new FileSystemResource(file));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasAccessToFile(String cookie, String fileName) {
        // TODO: Implement logic for checking if the user has access (e.g., based on cookie or user role)
        return true;
    }
}
