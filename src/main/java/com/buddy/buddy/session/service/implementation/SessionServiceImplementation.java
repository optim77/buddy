package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.session.DTO.GetSessionDTO;
import com.buddy.buddy.session.DTO.IpInfoDTO;
import com.buddy.buddy.session.entity.Session;
import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.IpService;
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
    private final IpService ipService;

    public SessionServiceImplementation(SessionRepository sessionRepository, IpService ipService) {
        this.sessionRepository = sessionRepository;
        this.ipService = ipService;
    }

    @Override
    public ResponseEntity<HttpStatus> createSession(User user, HttpServletRequest request, String token) {
        try{
            Session session = new Session();
            session.setUser(user);
            session.setSession(token);
            session.setIp(request.getRemoteAddr());
            session.setAgent(request.getHeader("User-Agent"));
            session.setStartTime(LocalDateTime.now());
            session.setEndTime(LocalDateTime.now().plusDays(30));
            try{
                IpInfoDTO info = ipService.getIpInfo(request.getRemoteAddr());
                session.setCountry(info.getCountry());
                session.setCity(info.getCity());
                session.setCountryCode(info.getCountryCode());
                session.setIsp(info.getIsp());
                session.setRegion(info.getRegion());
                session.setTimezone(info.getTimezone());
                session.setLatitude(info.getLatitude());
                session.setLongitude(info.getLongitude());
            } catch (Exception ignored){
            }

            sessionRepository.save(session);
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
