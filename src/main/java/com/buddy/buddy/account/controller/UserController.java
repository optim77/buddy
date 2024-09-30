package com.buddy.buddy.account.controller;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/hello_world")
    private String hello() {
        return "Hello World";
    }

    @PostMapping("/user/{userId}")
    private ResponseEntity<GetUserInformationDTO> getUserInformation(@PathVariable UUID userId) {
        return userRepository.findById(userId).map(user -> {
            if (user.isLocked()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User is locked");
            }
            GetUserInformationDTO getUserDTO = new GetUserInformationDTO(user);
            getUserDTO.setId(user.getId());
            return ResponseEntity.ok(getUserDTO);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
