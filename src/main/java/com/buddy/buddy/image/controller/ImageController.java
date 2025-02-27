package com.buddy.buddy.image.controller;


import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeAndTagsDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.DTO.UpdateImageDTO;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class ImageController {

    @Autowired
    private final ImageService imageService;

    public ImageController(ImageService imageService) {

        this.imageService = imageService;
    }

    @GetMapping("/image/{image_id}")
    private ResponseEntity<ImageWithUserLikeAndTagsDTO> getSingleImage(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.getImage(image_id, user);

    }

    @GetMapping("/image/user/{authorId}")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getAllUsersImages(@PathVariable UUID authorId, @AuthenticationPrincipal User user, Pageable pageable) {
        return imageService.getUserImages(authorId, user, pageable);
    }

    @GetMapping("/image/open/tag/{tag_id}")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getOpenImagesByTag(@PathVariable String tag_id, @AuthenticationPrincipal User user, Pageable pageable) {
        return imageService.getImagesByTag(tag_id, user, pageable);
    }

    @GetMapping("/image/open/random")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getOpenImagesRandom(@AuthenticationPrincipal User user, Pageable pageable) {
        return imageService.getImagesRandom(user, pageable);
    }

    @PostMapping("/image/upload")
    private ResponseEntity<UUID> uploadImage(@ModelAttribute UploadImageDTO uploadImageDTO, @AuthenticationPrincipal User user) {
        return imageService.uploadImage(uploadImageDTO, user);
    }

    @PutMapping("/image/update/{image_id}")
    private ResponseEntity<HttpStatus> updateImage(@ModelAttribute UpdateImageDTO uploadImageDTO, @PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.updateImage(uploadImageDTO, image_id, user);
    }

    @PostMapping("/image/delete/{image_id}")
    private ResponseEntity<HttpStatus> deleteImage(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.deleteImage(image_id, user);
    }

    @GetMapping("/loops")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getLoops(@AuthenticationPrincipal User user, Pageable pageable) {
        return imageService.getLoops(user, pageable);
    }
}
