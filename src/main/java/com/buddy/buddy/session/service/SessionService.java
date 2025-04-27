package com.buddy.buddy.session.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SessionService {
    boolean sessionExists(UUID sessionId);
    ResponseEntity<HttpStatus> logoutSingle(UUID userId, UUID sessionId);
    ResponseEntity<HttpStatus> logoutAll(UUID userId);
}
