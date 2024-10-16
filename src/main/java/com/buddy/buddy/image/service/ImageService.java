package com.buddy.buddy.image.service;

import com.buddy.buddy.image.DTO.GetImageDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ImageService {
    public ResponseEntity<GetImageDTO> getImage(UUID imageId);
}
