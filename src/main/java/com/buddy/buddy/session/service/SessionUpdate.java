package com.buddy.buddy.session.service;

import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.implementation.IpServiceImplementation;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionUpdate {

    private static final Logger logger = LoggerFactory.getLogger(SessionUpdate.class.getName());
    private final SessionRepository sessionRepository;

    public SessionUpdate(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSession() {
        logger.info("Updating session");
        try {
            int amount = sessionRepository.deleteOldSessions();
            logger.info("Deleted {} sessions", amount);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
