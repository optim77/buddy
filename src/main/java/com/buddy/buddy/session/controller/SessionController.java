package com.buddy.buddy.session.controller;


import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.session.DTO.GetSessionDTO;
import com.buddy.buddy.session.DTO.SessionLogoutRequestDTO;
import com.buddy.buddy.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
public class SessionController {

    @Autowired
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

//    @PostMapping("/session/create")
//    public ResponseEntity<HttpStatus> createSession(@AuthenticationPrincipal User user, HttpServletRequest request) {
//
//    }

    @GetMapping("/session")
    public ResponseEntity<Page<GetSessionDTO>> getSessions(@AuthenticationPrincipal User user, Pageable pageable) {
        return sessionService.getSessions(user, pageable);
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<HttpStatus> sessionExists(@PathVariable UUID id) {
        if (sessionService.sessionExists(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/session/logout/single")
    public ResponseEntity<HttpStatus> logoutSingle(@AuthenticationPrincipal User user, @RequestBody SessionLogoutRequestDTO sessionId) {
        return sessionService.logoutSingle(user.getId(), sessionId);
    }

    @PostMapping("/session/logout/all")
    public ResponseEntity<HttpStatus> logoutAll(@AuthenticationPrincipal User user) {
        return sessionService.logoutAll(user.getId());
    }
}
