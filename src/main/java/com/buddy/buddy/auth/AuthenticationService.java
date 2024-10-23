package com.buddy.buddy.auth;

import com.buddy.buddy.account.entity.Role;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.auth.DTO.AuthenticationRequest;
import com.buddy.buddy.auth.DTO.AuthenticationResponse;
import com.buddy.buddy.auth.DTO.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Random;
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

    public AuthenticationResponse register(RegisterRequest request){
        logger.info("Registering user");
        if (!StringUtils.hasText(request.getEmail()) || isValidEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Invalid email format")).getBody();
        }
        if(isValidPassword(request.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Password does not meet the requirements (8-32 characters, upper and lower case, special character)")).getBody();
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
            String token = jwtUtils.generateToken(user);
            return AuthenticationResponse.builder().token(token).build();
        }else{
            logger.debug("User already exists - {}", request.getEmail());
            return AuthenticationResponse.builder().token("").build();
        }

    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        logger.info("Authenticating user");
        if (!StringUtils.hasText(authenticationRequest.getEmail()) || isValidEmail(authenticationRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Invalid email format")).getBody();
        }
        if(isValidPassword(authenticationRequest.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Password does not meet the requirements (8-32 characters, upper and lower case, special character)")).getBody();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        User user = userRepository.findByUsernameOrEmail(authenticationRequest.getEmail(), authenticationRequest.getEmail());
        String token = jwtUtils.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse adminAuthenticate(AuthenticationRequest authenticationRequest) throws AccessDeniedException {
        logger.info("Authenticating admin user");
        if (!StringUtils.hasText(authenticationRequest.getEmail()) || isValidEmail(authenticationRequest.getEmail())) {
            logger.debug("Wrong email for admin login {}", authenticationRequest.getEmail());
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Invalid email format")).getBody();
        }
        if(isValidPassword(authenticationRequest.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse("Password does not meet the requirements (8-32 characters, upper and lower case, special character)")).getBody();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        User user = userRepository.findByUsernameOrEmail(authenticationRequest.getEmail(), authenticationRequest.getEmail());
        if (user.getRole().equals(Role.ADMIN)){
            String token = jwtUtils.generateToken(user);
            return AuthenticationResponse.builder().token(token).build();
        }else {
            logger.debug("Admin user does not exist - {}", authenticationRequest.getEmail());
            throw new AccessDeniedException("Access denied: User does not have admin privileges.");
        }

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