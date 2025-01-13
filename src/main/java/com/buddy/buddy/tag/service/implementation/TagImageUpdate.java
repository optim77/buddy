package com.buddy.buddy.tag.service.implementation;

import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.tag.entity.Tag;
import com.buddy.buddy.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagImageUpdate {

    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;

    public TagImageUpdate(TagRepository tagRepository, ImageRepository imageRepository) {
        this.tagRepository = tagRepository;
        this.imageRepository = imageRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateTagImages() {
        List<Tag> tags = tagRepository.findAll();
        tags.forEach(tag -> {

        });

    }
}
