package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeAndTagsDTO;
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
    static final String UPLOAD_DIR = "C:\\Dev\\res";

    @Value("${app.file.storage-path}")
    private String storagePath;

    @Value("${app.file.base-url}")
    private String baseUrl;

    public ImageServiceImplementation(ImageRepository imageRepository, SubscriptionRepository subscriptionRepository, TagRepository tagRepository) {
        this.imageRepository = imageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public ResponseEntity<ImageWithUserLikeAndTagsDTO> getImage(UUID imageId, User user) {
        if (user != null) {
            logger.info("Getting image by logged user");
            return imageRepository.findImageByIdWithUserAndLikeStatus(imageId, user.getId())
                    .map(dto -> {
                        Set<String> tags = imageRepository.findTagsByImageId(imageId);
                        ImageWithUserLikeAndTagsDTO imageWithUserLikeAndTagsDTO = new ImageWithUserLikeAndTagsDTO(dto, tags);
                        if (dto.isOpen() || isSubscriber(user.getId(), dto.getUserId())) {
                            return new ResponseEntity<>(imageWithUserLikeAndTagsDTO, HttpStatus.OK);
                        }
                        dto.setImageUrl("-");
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

        try {
            logger.debug("Creating and saving media");
            logger.debug("Received DTO: file={}, description={}, tags={}, open={}",
                    uploadImageDTO.getFile(),
                    uploadImageDTO.getDescription(),
                    uploadImageDTO.getTagSet(),
                    uploadImageDTO.isOpen());

            MultipartFile file = uploadImageDTO.getFile();
            validateFile(file);

            Path uploadPath = Paths.get(storagePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            UUID randomUUID = UUID.randomUUID();
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase();
            String savedFileName = randomUUID.toString() + "." + fileExtension;
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Image image = new Image();
            image.setUploadedDate(new Date());
            image.setDescription(uploadImageDTO.getDescription());
            image.setUser(user);
            image.setUrl(filePath.toString());
            image.setOpen(uploadImageDTO.isOpen());
            image.setId(randomUUID);

            Set<Tag> tags = uploadImageDTO.getTagSet().stream().map(tag -> {
                Optional<Tag> existingTag = tagRepository.findById(tag);
                if (existingTag.isPresent()) {
                    return existingTag.get();
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(tag);
                    return tagRepository.save(newTag);
                }
            }).collect(Collectors.toSet());
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

    private void validateFile(MultipartFile file) {
        final Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif", "mp4", "mov");
        final long maxFileSize = 100 * 1024 * 1024; // 100 MB

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is missing");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file extension. Allowed extensions: " + allowedExtensions);
        }

        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds the limit of " + (maxFileSize / (1024 * 1024)) + " MB");
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
            Set<String> currentTagNames = image.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            if (!uploadImageDTO.getTagSet().equals(currentTagNames)){
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
            }

            MultipartFile file = uploadImageDTO.getFile();

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
                logger.debug("Logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagLoggedUser(tag, user.getId(), pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }else {
                logger.debug("No logged user - getImagesByTag");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByTagNotLoggedUser(tag, pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesRandom(User user, Pageable pageable) {
        try {
            if (user != null){
                logger.debug("Logged user - getImagesRandom");
                Page<ImageWithUserLikeDTO> images = imageRepository.findOpenImagesByRandomLoggedUser(user.getId(), pageable);
                return new ResponseEntity<>(images, HttpStatus.OK);
            }else {
                logger.debug("No logged user - getImagesRandom");
                Page<ImageWithUserLikeDTO> image = imageRepository.findOpenImagesByRandomNotLoggedUser(pageable);
                return new ResponseEntity<>(image, HttpStatus.OK);
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    public boolean isSubscriber(UUID userID, UUID imageID) {
        Optional<User> user = imageRepository.findUserByPhotoId(imageID);
        if (user.isPresent()) {
            return subscriptionRepository.existsBySubscriberAndSubscribedTo(userID, user.get().getId());
        }
        logger.error("User not found in subscriptions");
        return false;
    }
}
