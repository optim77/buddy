package com.buddy.buddy.account.controller;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.GetUserProfileInformationDTO;
import com.buddy.buddy.account.DTO.ProfileInformationDTO;
import com.buddy.buddy.account.DTO.UpdateUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.service.AccountService;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class UserController {

    @Autowired
    private final AccountService accountService;
    @Autowired
    private UserRepository userRepository;

    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/hello_world")
    private String hello() {
        return "Hello World";
    }

    @GetMapping("/user/{userId}")
    private ResponseEntity<GetUserProfileInformationDTO> getUserInformation(@PathVariable UUID userId, @AuthenticationPrincipal User user) {
        return accountService.getAccount(userId, user);
    }

    @GetMapping("/user/list")
    private ResponseEntity<Page<GetUserInformationDTO>> getUserInformationListRandom(Pageable pageable) {
        return accountService.getUserListRandom(pageable);
    }

    @GetMapping("/user/list/{criteria}")
    private ResponseEntity<Page<GetUserInformationDTO>> getUserInformationListByCriteria(Pageable pageable, @PathVariable String criteria) {
        return accountService.getUserListByCriteria(criteria, pageable);
    }

    @GetMapping("/user/search")
    public ResponseEntity<Page<GetUserInformationDTO>> searchUsersByUsername(
            @RequestParam("username") String username,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.searchUser(username, pageable);
    }

    @PutMapping("/user/update")
    public ResponseEntity<HttpStatus> updateUser(@RequestBody UpdateUserInformationDTO userDTO, @AuthenticationPrincipal User principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return accountService.updateUser(userDTO, user);
    }

    @PostMapping("/user/change_password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody String password, @AuthenticationPrincipal User principal) {

        return accountService.updatePassword(password, principal);
    }
    @PostMapping("/user/delete")
    public ResponseEntity<HttpStatus> deleteUser(@AuthenticationPrincipal User principal) {
        return accountService.deleteUser(principal);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileInformationDTO> getProfileInformation(@AuthenticationPrincipal User user) {
        User fetched = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return accountService.getProfileInformation(fetched.getId());
    }

    @GetMapping("/profile/images")
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getProfilePhotos(@AuthenticationPrincipal User user, Pageable pageable) {
        return accountService.profilePhotos(user.getId(), pageable);
    }

}
