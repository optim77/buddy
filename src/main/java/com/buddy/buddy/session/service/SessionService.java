package com.buddy.buddy.session.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.session.DTO.GetSessionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SessionService {
    ResponseEntity<Page<GetSessionDTO>> getSessions(User user, Pageable pageable);
    boolean sessionExists(UUID sessionId);
    ResponseEntity<HttpStatus> logoutSingle(UUID userId, UUID sessionId);
    ResponseEntity<HttpStatus> logoutAll(UUID userId);
}
