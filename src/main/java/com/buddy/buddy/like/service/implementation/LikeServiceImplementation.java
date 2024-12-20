package com.buddy.buddy.like.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.implementation.ImageServiceImplementation;
import com.buddy.buddy.like.entity.Like;
import com.buddy.buddy.like.repository.LikeRepository;
import com.buddy.buddy.like.service.LikeService;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
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
    private final SubscriptionRepository subscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImplementation.class.getName());
    private ImageServiceImplementation imageService;

    public LikeServiceImplementation(LikeRepository likeRepository, ImageRepository imageRepository, SubscriptionRepository subscriptionRepository) {
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> likeImage(UUID image_id, User user) {
        try {
            Optional<Like> isLiked  = likeRepository.isLikedByUser(image_id, user.getId());
            if (isLiked.isPresent()) {
                likeRepository.deleteLikeByUser(image_id, user.getId());
                imageRepository.decrementLikesCount(image_id);
                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                Optional<Image> image = imageRepository.findById(image_id);
                if (image.isPresent()) {
                    Like like = new Like();
                    like.setUser(user);
                    like.setImage(image.get());
                    likeRepository.save(like);
                    imageRepository.incrementLikesCount(image_id);
                    logger.debug("User liked photo");
                    return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<Page<ImageWithUserLikeDTO>> likedImages(User user, Pageable pageable) {
        Page<ImageWithUserLikeDTO> image =  likeRepository.getLikedImagesByUser(user.getId(), pageable).map(dto -> {
            if (!dto.isOpen() || isSubscriber(user.getId(), dto.getUserId())){
                dto.setImageUrl("-");
            }
            logger.info("Returning image liked by user");
            return dto;
        });
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    public boolean isSubscriber(UUID userID, UUID imageID) {
        Optional<User> user = imageRepository.findUserByPhotoId(imageID);
        if (user.isPresent()) {
            return subscriptionRepository.existsBySubscriberAndSubscribedTo(userID, user.get().getId());
        }
        logger.info("User not found in subscriptions");
        return false;
    }
}
