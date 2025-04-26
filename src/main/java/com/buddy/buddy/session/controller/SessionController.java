package com.buddy.buddy.session.controller;


import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    //list sessions - auth!

    @GetMapping("/session/{id}")
    public ResponseEntity<HttpStatus> sessionExists(@PathVariable UUID id) {
        if (sessionService.sessionExists(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //create session

    //delete single session
    //delete all sessions
    @PostMapping("/session/logout/all")
    public ResponseEntity<HttpStatus> logoutAll(@AuthenticationPrincipal User user) {
        return sessionService.logoutAll(user.getId());
    }
}
