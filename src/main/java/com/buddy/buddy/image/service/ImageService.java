package com.buddy.buddy.image.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeAndTagsDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.DTO.UpdateImageDTO;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ImageService {
    ResponseEntity<ImageWithUserLikeAndTagsDTO> getImage(UUID imageId, User user);
    ResponseEntity<Page<ImageWithUserLikeDTO>> getUserImages(UUID authorId, User user, Pageable pageable);
    ResponseEntity<UUID> uploadImage(UploadImageDTO uploadImageDTO, User user);
    ResponseEntity<HttpStatus> updateImage(UpdateImageDTO uploadImageDTO, UUID image_id, User user);
    ResponseEntity<HttpStatus> deleteImage(UUID imageId, User user);
    ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesByTag(String tag, User user, Pageable pageable);
    ResponseEntity<Page<ImageWithUserLikeDTO>> getImagesRandom(User user, Pageable pageable);
    ResponseEntity<Page<ImageWithUserLikeDTO>> getLoops(User user, Pageable pageable);
}
