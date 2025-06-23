package com.buddy.buddy.auth;

import com.buddy.buddy.account.entity.Role;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.auth.DTO.AuthenticationRequest;
import com.buddy.buddy.auth.DTO.AuthenticationResponse;
import com.buddy.buddy.auth.DTO.RegisterRequest;
import com.buddy.buddy.exception.AuthOperationException;
import com.buddy.buddy.notification.DTO.RegisterNotificationRequest;
import com.buddy.buddy.notification.Service.NotificationProducer;
import com.buddy.buddy.session.repository.SessionRepository;
import com.buddy.buddy.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

import org.slf4j.Logger;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;


    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request){
        logger.info("Registering user");
        if (!StringUtils.hasText(request.getEmail()) || isValidEmail(request.getEmail())) {
            return new ResponseEntity<>(new AuthenticationResponse("", "Invalid email format", ""), HttpStatus.BAD_REQUEST);
        }
        if(isValidPassword(request.getPassword())) {
            return new ResponseEntity<>(new AuthenticationResponse("", "Password does not meet the requirements (8-32 characters, upper and lower case, special character)", ""), HttpStatus.BAD_REQUEST);
        }
        boolean isExistEmail = userRepository.existsByEmail(request.getEmail());
        if(!isExistEmail){
            logger.debug("User not found - creating a new user");
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUsername(request.getEmail().split("@")[0]);
            //change in prod to false
            user.setActive(true);
            user.setRole(Role.USER);
            userRepository.save(user);
            String token = jwtUtils.generateToken(user, UUID.randomUUID());
            return new ResponseEntity<>(AuthenticationResponse.builder().token(token).build(), HttpStatus.CREATED);
        }else{
            logger.debug("User already exists - {}", request.getEmail());
            return new ResponseEntity<>(new AuthenticationResponse("", "Email is already in use", ""), HttpStatus.CONFLICT);
        }

    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest request){
        logger.info("Authenticating user");
        if (!StringUtils.hasText(authenticationRequest.getEmail()) || isValidEmail(authenticationRequest.getEmail())) {
            throw new AuthOperationException("Invalid email format", HttpStatus.UNAUTHORIZED);
        }
        if(isValidPassword(authenticationRequest.getPassword())) {
            throw new AuthOperationException("Password does not meet the requirements", HttpStatus.UNAUTHORIZED);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()));
            User user = userRepository.findByUsernameOrEmail(authenticationRequest.getEmail(), authenticationRequest.getEmail());
            UUID sessionId = UUID.randomUUID();
            String token = jwtUtils.generateToken(user, sessionId);
            sessionService.createSession(user, request, token, sessionId);
            return AuthenticationResponse.builder().token(token).userId(user.getId().toString()).build();
        } catch (AuthenticationException e){
            throw new AuthOperationException("Invalid email format", HttpStatus.UNAUTHORIZED);
        }

    }

    public AuthenticationResponse adminAuthenticate(AuthenticationRequest authenticationRequest) throws AccessDeniedException {
        logger.info("Authenticating admin user");
        if (!StringUtils.hasText(authenticationRequest.getEmail()) || isValidEmail(authenticationRequest.getEmail())) {
            logger.debug("Wrong email for admin login {}", authenticationRequest.getEmail());
            throw new AuthOperationException("Invalid email format", HttpStatus.UNAUTHORIZED);
        }
        if(isValidPassword(authenticationRequest.getPassword())) {
            throw new AuthOperationException("Password does not meet the requirements (8-32 characters, upper and lower case, special character)", HttpStatus.UNAUTHORIZED);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        User user = userRepository.findByUsernameOrEmail(authenticationRequest.getEmail(), authenticationRequest.getEmail());
        if (user.getRole().equals(Role.ADMIN)){

            String token = jwtUtils.generateToken(user, UUID.randomUUID());
            return AuthenticationResponse.builder().token(token).userId(user.getId().toString()).build();
        }else {
            logger.debug("Admin user does not exist - {}", authenticationRequest.getEmail());
            throw new AuthOperationException("Access denied: User does not have admin privileges.", HttpStatus.UNAUTHORIZED);
        }

    }

    public ResponseEntity<HttpStatus> authorization(String password, User user, String request){
        if (userRepository.checkPassword(user.getEmail(), passwordEncoder.encode(password)) && sessionRepository.checkSession(request, user.getId())){
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return !email.matches(emailRegex);
    }
    public boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";
        return password == null || !password.matches(passwordRegex);
    }
}
