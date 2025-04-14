package com.buddy.buddy.file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface FileService {

    ResponseEntity<Resource> getFile(String filename, String cookie);
}
