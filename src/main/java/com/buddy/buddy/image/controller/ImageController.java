package com.buddy.buddy.image.controller;


import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ResponseEntity<GetImageDTO> getSingleImage(@PathVariable UUID image_id) {
        return imageService.getImage(image_id);

    }
}
