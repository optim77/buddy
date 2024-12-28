package com.buddy.buddy.favorite.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.favorite.controller.FavoriteController;
import com.buddy.buddy.favorite.entity.Favorite;
import com.buddy.buddy.favorite.repository.FavoriteRepository;
import com.buddy.buddy.favorite.service.FavoriteService;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class FavoriteServiceImplementation implements FavoriteService {


    private final FavoriteRepository favoriteRepository;
    private final ImageRepository imageRepository;

    public FavoriteServiceImplementation(FavoriteRepository favoriteRepository, ImageRepository imageRepository) {
        this.favoriteRepository = favoriteRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public ResponseEntity<HttpStatus> createOrDeleteFavorite(UUID media_id, User user) {
        if (media_id == null || user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Favorite> favorite = favoriteRepository.findByid(media_id);
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            Favorite favoriteEntity = new Favorite();
            Optional<Image> image = imageRepository.findById(media_id);
            if (image.isPresent()) {
                favoriteEntity.setImage(image.get());
                favoriteEntity.setUser(user);
                favoriteRepository.save(favoriteEntity);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getFavoritesMedia(User user, Pageable pageable) {
        if (user != null) {
            Page<ImageWithUserLikeDTO> media = favoriteRepository.getUserFavorite(user.getId(), pageable);
            return new ResponseEntity<>(media, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
