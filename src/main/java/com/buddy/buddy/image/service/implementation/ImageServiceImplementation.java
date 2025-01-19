package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.image.DTO.ImageWithUserLikeAndTagsDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.DTO.UpdateImageDTO;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.entity.MediaType;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.ImageService;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import com.buddy.buddy.tag.entity.Tag;
import com.buddy.buddy.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ImageServiceImplementation implements ImageService {

    private final ImageRepository imageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImplementation.class.getName());
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ImageServiceHelper imageServiceHelper;

    @Value("${app.file.storage-path}")
    private String storagePath;

    @Value("${app.file.base-url}")
    private String baseUrl;

    public ImageServiceImplementation(ImageRepository imageRepository,
                                      SubscriptionRepository subscriptionRepository,
                                      TagRepository tagRepository,
                                      UserRepository userRepository,
                                      ImageServiceHelper imageServiceHelper) {
        this.imageRepository = imageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.imageServiceHelper = imageServiceHelper;
    }

    @Override
    public ResponseEntity<ImageWithUserLikeAndTagsDTO> getImage(UUID imageId, User user) {
        if (user != null) {
            logger.info("Getting image by logged user");
            return imageRepository.findImageByIdWithUserAndLikeStatus(imageId, user.getId())
                    .map(dto -> {
                        Set<String> tags = imageRepository.findTagsByImageId(imageId);
                        ImageWithUserLikeAndTagsDTO imageWithUserLikeAndTagsDTO = new ImageWithUserLikeAndTagsDTO(dto, tags);
                        if (!dto.isOpen() && !isSubscriber(user.getId(), dto.getUserId()) && !user.getId().equals(dto.getUserId())) {
                            imageWithUserLikeAndTagsDTO.setImageUrl("");
                        }
                        return new ResponseEntity<>(imageWithUserLikeAndTagsDTO, HttpStatus.OK);
                    })
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        } else {
            logger.info("Getting image by not logged user");
            return imageRepository.findImageByIdWithUserForNotLoggedUser(imageId)
                    .map(dto -> {
                        Set<String> tags = imageRepository.findTagsByImageId(imageId);
                        ImageWithUserLikeAndTagsDTO imageWithUserLikeAndTagsDTO = new ImageWithUserLikeAndTagsDTO(dto, tags);
                        if (dto.isOpen()) {
                            return new ResponseEntity<>(imageWithUserLikeAndTagsDTO, HttpStatus.OK);
                        }
                        dto.setImageUrl("-");
                        return new ResponseEntity<>(imageWithUserLikeAndTagsDTO, HttpStatus.OK);
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
                Page<ImageWithUserLikeDTO> images = imageRepository.findImagesByUserIdWithUserAndLikeStatus(authorId, user.getId(), pageable).map(image -> {
                    //check if visitor is author also || user.getId().equals(image.getUserId())
                    if (!image.isOpen() && !isSubscriber && !user.getId().equals(image.getUserId())) {
                        image.setImageUrl("");
                    }
                    return image;
                });
                return new ResponseEntity<>(images, HttpStatus.OK);
            } else {
                logger.info("No logged user");
                Page<ImageWithUserLikeDTO> images = imageRepository.findImagesByUserIdForNotLoggedUser(authorId, pageable).map(image -> {
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
        // TODO: Blur video somehow
        try {
            logger.info("Creating and saving media");

            imageServiceHelper.validateUploadImageDTO(uploadImageDTO);

            MultipartFile file = uploadImageDTO.getFile();
            ImageServiceHelper.validateFile(file);

            UUID randomUUID = UUID.randomUUID();
            String fileExtension = imageServiceHelper.getFileExtension(file);
            Path savedFilePath = imageServiceHelper.saveFile(file, randomUUID, fileExtension);
            String blurredUrl = "";
            String blurredFilePath = null;
            if (!uploadImageDTO.isOpen() && !imageServiceHelper.isVideo(fileExtension)) {
                blurredUrl = imageServiceHelper.createBlurredImage(savedFilePath, fileExtension);
                Path uploadPath = Paths.get(storagePath);
                String blurredSavedFileName = blurredUrl + "." + fileExtension;
                blurredFilePath = uploadPath.resolve(blurredSavedFileName).toString();
            }

            Image image = imageServiceHelper.createImageEntity(uploadImageDTO, user, savedFilePath.toString(), blurredFilePath, fileExtension, randomUUID);
            user.setPosts(user.getPosts() + 1);
            userRepository.save(user);

            Set<Tag> tags = processTags(uploadImageDTO.getTagSet());
            image.setTags(tags);


            imageRepository.save(image);
            return new ResponseEntity<>(randomUUID, HttpStatus.CREATED);

        } catch (IOException e) {
            logger.error("File upload failed: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    @Override
    public ResponseEntity<HttpStatus> updateImage(UpdateImageDTO uploadImageDTO, UUID image_id, User user) {
        try {
            Image image = imageRepository.findById(image_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
            if (!uploadImageDTO.getDescription().equals(image.getDescription())) {
                image.setDescription(uploadImageDTO.getDescription());
            }
            if (uploadImageDTO.isOpen() != image.isOpen()) {
                image.setOpen(uploadImageDTO.isOpen());
            }
            List<String> currentTagNames = image.getTags().stream()
                    .map(Tag::getName)
                    .toList();
            if (!uploadImageDTO.getTagSet().equals(currentTagNames)) {
                Set<Tag> tags = uploadImageDTO.getTagSet().stream().map(tag -> {
                    Optional<Tag> existed_tag = tagRepository.findById(tag);
                    if (existed_tag.isPresent()) {
                        return existed_tag.get();
                    } else {
                        Tag new_tag = new Tag();
                        new_tag.setName(tag);
                        new_tag.setCount(1);
                        return tagRepository.save(new_tag);
                    }
                }).collect(Collectors.toSet());
                image.setTags(tags);
                List<String> tagsToRemove = new ArrayList<>(currentTagNames);
                tagsToRemove.removeAll(uploadImageDTO.getTagSet());
                tagsToRemove.forEach(tag -> {
                    Optional<Tag> existingTag = tagRepository.findByName(tag);
                    existingTag.ifPresent(value -> {
                        value.setCount(existingTag.get().getCount() - 1);
                        tagRepository.save(existingTag.get());
                    });
                });
            }

            imageRepository.save(image);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> deleteImage(UUID imageId, User user) {
        if (user != null) {
            logger.debug("Deleting image by logged user, image {}", imageId);
            try {
                imageRepository.setDeleteImageById(imageId);
                Optional<Image> image = imageRepository.findById(imageId);
                image.ifPresent(value -> {
                    value.getTags().forEach(tag -> {
                        Optional<Tag> tag1 = tagRepository.findByName(tag.getName());
                        tag1.ifPresent(tag2 -> {
                            tag2.setCount(tag2.getCount() - 1);
                            tagRepository.save(tag2);
                        });
                    });
                });
                user.setPosts(user.getPosts() - 1);
                userRepository.save(user);

                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesByTag(String tag, User user, Pageable pageable) {
        try {
            if (user != null) {
                logger.debug("Logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagLoggedUser(tag, user.getId(), pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            } else {
                logger.debug("No logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagNotLoggedUser(tag, pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesRandom(User user, Pageable pageable) {
        try {
            if (user != null) {
                logger.info("Logged user - getImagesRandom");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByRandomLoggedUser(user.getId(), pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            } else {
                logger.info("No logged user - getImagesRandom");
                Page<ImageWithUserLikeDTO> image = imageRepository.findOpenImagesByRandomNotLoggedUser(pageable);
                return new ResponseEntity<>(image, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getLoops(User user, Pageable pageable) {
        try {
            if (user != null) {
                logger.debug("Logged user - getLoops");
                Page<ImageWithUserLikeDTO> videos = imageRepository.findOpenVideosByRandomLoggedUser(user.getId(), pageable);
                return new ResponseEntity<>(videos, HttpStatus.OK);
            } else {
                logger.debug("Not logged user - getLoops");
                Page<ImageWithUserLikeDTO> video = imageRepository.findOpenVideosByRandomNotLoggedUser(pageable);
                return new ResponseEntity<>(video, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    public boolean isSubscriber(UUID userID, UUID imageID) {
        Optional<User> user = imageRepository.findUserByPhotoId(imageID);
        if (user.isPresent()) {
            return subscriptionRepository.existsBySubscriberAndSubscribedTo(userID, user.get().getId());
        }
        logger.info("User not found in subscriptions");
        return false;
    }


    private Set<Tag> processTags(Set<String> tagSet) {
        return tagSet.stream().map(tag -> {
            Optional<Tag> existingTag = tagRepository.findById(tag);
            if (existingTag.isPresent()) {
                Tag tagEntity = existingTag.get();
                tagEntity.setCount(tagEntity.getCount() + 1);
                return tagRepository.save(tagEntity);
            } else {
                Tag newTag = new Tag();
                newTag.setName(tag);
                newTag.setCount(1);
                return tagRepository.save(newTag);
            }
        }).collect(Collectors.toSet());
    }



}
