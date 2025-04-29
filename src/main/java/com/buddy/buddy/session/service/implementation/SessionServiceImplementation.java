package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.session.DTO.GetSessionDTO;
import com.buddy.buddy.session.entity.Session;
import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionServiceImplementation implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImplementation(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public ResponseEntity<HttpStatus> createSession(User user, HttpServletRequest request, String token) {
        try{
            Session session = new Session();
            session.setUser(user);
            session.setSession(token);
            session.setIp(request.getRemoteAddr());
            session.setAgent(request.getHeader("User-Agent"));
            session.setCountry(request.getHeader("Accept-Language"));
            session.setStartTime(LocalDateTime.now());
            session.setEndTime(LocalDateTime.now().plusDays(30));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @Override
    public ResponseEntity<Page<GetSessionDTO>> getSessions(User user, Pageable pageable) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            return ResponseEntity.ok(sessionRepository.getSessions(user.getId(), pageable));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean sessionExists(UUID sessionId) {
        return sessionRepository.existsBySessionId(sessionId);
    }

    @Override
    public ResponseEntity<HttpStatus> logoutSingle(UUID userId, UUID sessionId) {
        try {
            sessionRepository.deleteOneByUserId(userId, sessionId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
