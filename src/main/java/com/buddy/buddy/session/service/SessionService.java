package com.buddy.buddy.session.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SessionService {

    boolean sessionExists(UUID sessionId);
}
