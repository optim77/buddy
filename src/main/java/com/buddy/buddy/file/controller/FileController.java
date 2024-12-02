package com.buddy.buddy.file.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class FileController {


    @Autowired
    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, @CookieValue("buddy-token") String cookie) {
        if (fileName == null || cookie == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return fileService.getFile(fileName, cookie);
    }


}
