package com.buddy.buddy.like.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.like.service.LikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like/image/{image_id}")
    private ResponseEntity<HttpStatus> getLikePhoto(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return likeService.likeImage(image_id, user);
    }

    @GetMapping("/like/images")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getLikedImages(@AuthenticationPrincipal User user, Pageable pageable) {
        return likeService.likedImages(user, pageable);
    }
}
