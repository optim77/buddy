package com.buddy.buddy.favorite.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.favorite.service.FavoriteService;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
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
public class FavoriteController {

    @Autowired
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/favorite/add/{media_id}")
    private ResponseEntity<HttpStatus> createOrDeleteFavorite(@PathVariable UUID media_id, @AuthenticationPrincipal User user){
        return favoriteService.createOrDeleteFavorite(media_id, user);
    }

    @GetMapping("/favorite/list")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getFavorites(@AuthenticationPrincipal User user, Pageable pageable){
        return favoriteService.getFavoritesMedia(user, pageable);
    }
}
