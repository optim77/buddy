package com.buddy.buddy.favorite.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface FavoriteService {

    ResponseEntity<HttpStatus> createOrDeleteFavorite(UUID media_id, User user);
    ResponseEntity<Page<ImageWithUserLikeDTO>> getFavoritesMedia(User user, Pageable pageable);
}
