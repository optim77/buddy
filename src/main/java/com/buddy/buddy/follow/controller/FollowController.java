package com.buddy.buddy.follow.controller;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.follow.servce.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping(produces = "application/json")
public class FollowController {

    @Autowired
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow")
    public ResponseEntity<HttpStatus> getFollowOrUnfollow(@AuthenticationPrincipal User user, @RequestBody UUID followedTo){
        return followService.followOrUnfollow(user, followedTo);
    }

    @GetMapping("/follows")
    public ResponseEntity<Page<GetUserInformationDTO>> getFollows(@AuthenticationPrincipal User user, Pageable pageable){
        return followService.follows(user, pageable);
    }
}
