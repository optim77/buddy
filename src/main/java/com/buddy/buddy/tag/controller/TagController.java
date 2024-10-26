package com.buddy.buddy.tag.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.tag.DTO.AddTagDTO;
import com.buddy.buddy.tag.entity.Tag;
import com.buddy.buddy.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json")
public class TagController {

    @Autowired
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags/{tag_name}")
    private ResponseEntity<Page<Tag>> getSearchTag(@PathVariable String tag_name, Pageable pageable) {
        return tagService.getSearchTag(tag_name, pageable);
    }


    @PostMapping("/tag/add")
    private ResponseEntity<HttpStatus> addTag(@RequestBody AddTagDTO addTagDTO) {
        return tagService.addTag(addTagDTO);
    }

    @GetMapping("/tags/all")
    private ResponseEntity<Page<Tag>> getAllTags(Pageable pageable) {
        return tagService.getAllTags(pageable);
    }


}
