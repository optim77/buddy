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

    public TagImageUpdate(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateTagImages() {
        List<Tag> tags = tagRepository.findAll();
        tags.forEach(tag -> {
            List<String> images = tagRepository.getMediaForTag(tag.getName());
            tag.setFirstImage(images.get(0));
            tag.setFirstImage(images.get(1));
            tag.setFirstImage(images.get(2));
            tagRepository.save(tag);
        });

    }
}
