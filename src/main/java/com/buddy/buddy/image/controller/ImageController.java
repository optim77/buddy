package com.buddy.buddy.image.controller;


import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import com.buddy.buddy.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private ResponseEntity<ImageWithUserLikeDTO> getSingleImage(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.getImage(image_id, user);

    }

    @GetMapping("/image/user/{authorId}")
    private ResponseEntity<Page<ImageWithUserLikeDTO>> getAllUsersImages(@PathVariable UUID authorId, @AuthenticationPrincipal User user, Pageable pageable) {
        return imageService.getUserImages(authorId, user, pageable);
    }

    //@GetMapping("/image/open/tag/{tag_id}")
    //@GetMapping("/image/open/criteria/{random/popularity}")

    @PostMapping("/image/upload")
    private ResponseEntity<UUID> uploadImage(@RequestBody UploadImageDTO uploadImageDTO, @AuthenticationPrincipal User user) {
        return imageService.uploadImage(uploadImageDTO, user);
    }

    @PutMapping("/image/update/{image_id}")
    private ResponseEntity<HttpStatus> updateImage(@RequestBody UploadImageDTO uploadImageDTO, @PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.updateImage(uploadImageDTO, image_id, user);
    }

    @PostMapping("/image/delete/{image_id}")
    private ResponseEntity<HttpStatus> deleteImage(@PathVariable UUID image_id, @AuthenticationPrincipal User user) {
        return imageService.deleteImage(image_id, user);
    }
}
