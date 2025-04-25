package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class SessionServiceImplementation implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImplementation(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean sessionExists(UUID sessionId) {
        return sessionRepository.existsBySessionId(sessionId);
    }
}
