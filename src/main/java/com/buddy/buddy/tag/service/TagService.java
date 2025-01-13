package com.buddy.buddy.tag.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.tag.DTO.AddTagDTO;
import com.buddy.buddy.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface TagService {

    ResponseEntity<HttpStatus> addTag(AddTagDTO addTagDTO);
    ResponseEntity<Page<Tag>> getAllTags(Pageable pageable);
    ResponseEntity<Page<Tag>> getSearchTag(String tagName, Pageable pageable);
    ResponseEntity<Page<ImageWithUserLikeDTO>> mediaTag(String tagName, User user, Pageable pageable);
}
