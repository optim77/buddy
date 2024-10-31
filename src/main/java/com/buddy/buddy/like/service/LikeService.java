package com.buddy.buddy.like.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface LikeService {

    ResponseEntity<HttpStatus> likeImage(UUID image_id, User user);
    ResponseEntity<Page<ImageWithUserLikeDTO>> likedImages(UUID user_id, Pageable pageable);

}
