package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.auth.JwtUtils;
import com.buddy.buddy.exception.AuthOperationException;
import com.buddy.buddy.exception.SessionOperationException;
import com.buddy.buddy.notification.DTO.LogoutNotificationRequest;
import com.buddy.buddy.notification.DTO.RegisterNotificationRequest;
import com.buddy.buddy.notification.Service.NotificationProducer;
import com.buddy.buddy.session.DTO.GetSessionDTO;
import com.buddy.buddy.session.DTO.SessionLogoutRequestDTO;
import com.buddy.buddy.session.entity.Session;
import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.IpService;
import com.buddy.buddy.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionServiceImplementation implements SessionService {

    private final SessionRepository sessionRepository;
    private final IpService ipService;
    private final static Logger logger = LoggerFactory.getLogger(SessionServiceImplementation.class);
    private final NotificationProducer notificationProducer;
    private final JwtUtils jwtUtils;

    public SessionServiceImplementation(SessionRepository sessionRepository, IpService ipService, NotificationProducer notificationProducer, JwtUtils jwtUtils) {
        this.sessionRepository = sessionRepository;
        this.ipService = ipService;
        this.notificationProducer = notificationProducer;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void createSession(User user, HttpServletRequest request, String token, UUID sessionId) {
        try{
            logger.debug("Create session");
            Session session = new Session();
            session.setId(sessionId);
            session.setUser(user);
            session.setSession(token);
            session.setIp(request.getRemoteAddr());
            session.setAgent(request.getHeader("User-Agent"));
            session.setStartTime(LocalDateTime.now());
            session.setEndTime(LocalDateTime.now().plusDays(30));
//            try {
//                IpInfoDTO info = ipService.getIpInfo(request.getRemoteAddr());
//                session.setCountry(info.getCountry());
//                session.setCity(info.getCity());
//                session.setCountryCode(info.getCountryCode());
//                session.setIsp(info.getIsp());
//                session.setRegion(info.getRegion());
//                session.setTimezone(info.getTimezone());
//                session.setLatitude(info.getLatitude());
//                session.setLongitude(info.getLongitude());
//            } catch (Exception ignored){
//            }

            sessionRepository.save(session);
            this.registerSessionInNotificationService(user, sessionId, token);
        }catch (Exception e){
            logger.debug("Exception creating session {}", e.getMessage());
            throw new AuthOperationException("Cannot create session", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Page<GetSessionDTO>> getSessions(User user, Pageable pageable) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            logger.debug("Get single session");
            return ResponseEntity.ok(sessionRepository.getSessions(user.getId(), pageable));
        } catch (Exception e) {
            logger.debug("Exception getting session {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean sessionExists(UUID sessionId) {
        logger.debug("Check if session exists");
        return sessionRepository.existsBySessionId(sessionId);
    }

    @Override
    public ResponseEntity<HttpStatus> logoutSingle(User user, SessionLogoutRequestDTO sessionId) {
        try {
            logger.debug("Logout single session");
            sessionRepository.deleteOneByUserId(user.getId(), sessionId.getSessionId(), sessionId.getSession());
            this.logoutSessionInNotificationService(user, sessionId.getSessionId());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("Exception logout single session {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> logoutAll(UUID userId) {
        try {
            logger.debug("Logout all sessions");
            sessionRepository.deleteAllByUserId(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("Exception logout all session {}", e.getMessage());
            throw new SessionOperationException("Failed to logout all sessions", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void registerSessionInNotificationService(User user, UUID sessionId, String token){
        try {
            RegisterNotificationRequest registerNotificationRequest = new RegisterNotificationRequest();
            registerNotificationRequest.setUserId(user.getId());
            registerNotificationRequest.setSessionId(sessionId);
            registerNotificationRequest.setSub(user.getUsername());
            registerNotificationRequest.setIat(jwtUtils.extractIssuedAt(token));
            registerNotificationRequest.setExp(jwtUtils.extractExpirationTime(token));
            notificationProducer.registerNotification(registerNotificationRequest);
        } catch (Exception e){
            throw new SessionOperationException(
                    "Cannot register session in notification service",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void logoutSessionInNotificationService(User user, UUID session){
        logger.debug("Logout session {} from notification service", session);
        try {
            LogoutNotificationRequest logoutNotificationRequest = new LogoutNotificationRequest();
            logoutNotificationRequest.setUserId(user.getId());
            logoutNotificationRequest.setSessionId(session);
            logoutNotificationRequest.setSub(user.getUsername());
            notificationProducer.logoutNotification(logoutNotificationRequest);
        } catch (Exception e){
            logger.error("Exception logout session {}", e.getMessage());
            throw new SessionOperationException(
                    "Cannot logout session from notification service",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    // TODO Implement notification request for deleting all session for user
    private void logoutAllSessionInNotificationService(User user){
        List<Session> sessions =  sessionRepository.getAllByUserId(user.getId());
    }

}
