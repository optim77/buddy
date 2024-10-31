package com.buddy.buddy.like.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.implementation.ImageServiceImplementation;
import com.buddy.buddy.like.entity.Like;
import com.buddy.buddy.like.repository.LikeRepository;
import com.buddy.buddy.like.service.LikeService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class LikeServiceImplementation implements LikeService {

    private final LikeRepository likeRepository;
    private final ImageRepository imageRepository;
    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImplementation.class.getName());
    private ImageServiceImplementation imageService;

    public LikeServiceImplementation(LikeRepository likeRepository, ImageRepository imageRepository) {
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> likeImage(UUID image_id, User user) {
        try {
            boolean isLiked = likeRepository.isLikedByUser(image_id, user.getId());
            if (isLiked) {
                likeRepository.deleteLikeByUser(image_id, user.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                Optional<Image> image = imageRepository.findById(image_id);
                if (image.isPresent()) {
                    Like like = new Like();
                    like.setUser(user);
                    like.setImage(image.get());
                    likeRepository.save(like);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                logger.debug("Image to like is not found - {}", image_id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> likedImages(UUID user_id, Pageable pageable) {
        Page<ImageWithUserLikeDTO> image =  likeRepository.getLikedImagesByUser(user_id, pageable).map(dto -> {
            if (!dto.isOpen() || imageService.isSubscriber(user_id, dto.getUserId())){
                dto.setImageUrl("-");
            }
            return dto;
        });
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}
