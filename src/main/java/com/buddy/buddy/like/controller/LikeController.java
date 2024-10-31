package com.buddy.buddy.like.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.like.service.LikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/like/image/{image_id}")
    private ResponseEntity<HttpStatus> getLikePhoto(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return likeService.likeImage(image_id, user);
    }

    @GetMapping("/like/images/{user_id}")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getLikedImages(@PathVariable UUID user_id, Pageable pageable) {
        return likeService.likedImages(user_id, pageable);
    }
}