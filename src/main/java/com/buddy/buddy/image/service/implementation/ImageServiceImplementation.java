package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.ImageService;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import com.buddy.buddy.tag.entity.Tag;
import com.buddy.buddy.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ImageServiceImplementation implements ImageService {

    private final ImageRepository imageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImplementation.class.getName());
    private final TagRepository tagRepository;

    public ImageServiceImplementation(ImageRepository imageRepository, SubscriptionRepository subscriptionRepository, TagRepository tagRepository) {
        this.imageRepository = imageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public ResponseEntity<ImageWithUserLikeDTO> getImage(UUID imageId, User user) {
        if (user != null) {
            logger.info("Getting image by logged user");
            return imageRepository.findImageByIdWithUserAndLikeStatus(imageId, user.getId())
                    .map(dto -> {
                        if (dto.isOpen() || isSubscriber(user.getId(), dto.getUserId())) {
                            return new ResponseEntity<>(dto, HttpStatus.OK);
                        }
                        dto.setImageUrl("-");
                        return new ResponseEntity<>(dto, HttpStatus.OK);
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        } else {
            logger.info("Getting image by not logged user");
            return imageRepository.findImageByIdWithUserForNotLoggedUser(imageId)
                    .map(dto -> {
                        if (dto.isOpen()) {
                            return new ResponseEntity<>(dto, HttpStatus.OK);
                        }
                        dto.setImageUrl("-");
                        return new ResponseEntity<>(dto, HttpStatus.OK);
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        }
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getUserImages(UUID authorId, User user, Pageable pageable) {
        try {
            logger.info("Getting user images");
            if (user != null) {
                boolean isSubscriber = isSubscriber(user.getId(), authorId);
                Page<ImageWithUserLikeDTO> images =  imageRepository.findImagesByUserIdWithUserAndLikeStatus(authorId, user.getId(), pageable).map(image -> {
                    if (!image.isOpen() || !isSubscriber) {
                        image.setImageUrl("-");
                    }
                    return image;
                });
                return new ResponseEntity<>(images, HttpStatus.OK);
            } else {
                logger.info("No logged user");
                Page<ImageWithUserLikeDTO> images =  imageRepository.findImagesByUserIdForNotLoggedUser(authorId, pageable).map(image -> {
                    if (!image.isOpen()) {
                        image.setImageUrl("-");
                    }
                    logger.info(image.toString());
                    return image;
                });
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<UUID> uploadImage(UploadImageDTO uploadImageDTO, User user) {
        // TODO: implement adding tag to image
        try {
            logger.debug("Creating and saving media");
            Image image = new Image();
            image.setUploadedDate(new Date());
            Set<Tag> tags = uploadImageDTO.getTagSet().stream().map(tag -> {
                Optional<Tag> existed_tag = tagRepository.findById(tag);
                if (existed_tag.isPresent()) {
                    return existed_tag.get();
                }else {
                    Tag new_tag = new Tag();
                    new_tag.setName(tag);
                    return tagRepository.save(new_tag);
                }
            }).collect(Collectors.toSet());
            image.setTags(tags);
            image.setDescription(uploadImageDTO.getDescription());
            image.setUser(user);
            //check and save file
            image.setUrl("url");
            image.setOpen(uploadImageDTO.isOpen());
            UUID imageId = imageRepository.save(image).getId();
            return new ResponseEntity<>(imageId, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    @Override
    public ResponseEntity<HttpStatus> updateImage(UploadImageDTO uploadImageDTO, UUID image_id, User user){
        // TODO: update tags
        try {
            Image image = imageRepository.findById(image_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            if (!uploadImageDTO.getDescription().equals(image.getDescription())) {
                image.setDescription(uploadImageDTO.getDescription());
            }
            if (uploadImageDTO.isOpen() != image.isOpen()) {
                image.setOpen(uploadImageDTO.isOpen());
            }
            imageRepository.save(image);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> deleteImage(UUID imageId, User user) {
        if (user != null) {
            logger.debug("Deleting image by logged user, image {}", imageId);
            try{
                imageRepository.setDeleteImageById(imageId);
                return new ResponseEntity<>(HttpStatus.OK);
            }catch (Exception e) {
                logger.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesByTag(String tag, User user, Pageable pageable) {
        try {
            if (user != null){
                logger.info("Logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagLoggedUser(tag, user.getId(), pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }else {
                logger.info("No logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagNotLoggedUser(tag, pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }


    private boolean isSubscriber(UUID userID, UUID imageID) {
        Optional<User> user = imageRepository.findUserByPhotoId(imageID);
        if (user.isPresent()) {
            return subscriptionRepository.existsBySubscriberAndSubscribedTo(userID, user.get().getId());
        }
        logger.error("User not found in subscriptions");
        return false;
    }
}
