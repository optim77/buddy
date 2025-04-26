package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionServiceImplementation implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImplementation(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean sessionExists(UUID sessionId) {
        return sessionRepository.existsBySessionId(sessionId);
    }

    @Override
    public ResponseEntity<HttpStatus> logoutAll(UUID userId) {
        try {
            sessionRepository.deleteAllByUserId(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
