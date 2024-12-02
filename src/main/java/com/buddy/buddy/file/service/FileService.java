package com.buddy.buddy.file.service;

import com.buddy.buddy.account.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface FileService {

    ResponseEntity<Resource> getFile(String filename, String cookie);
}
