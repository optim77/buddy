package com.buddy.buddy.tag.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.tag.DTO.AddTagDTO;
import com.buddy.buddy.tag.entity.Tag;
import com.buddy.buddy.tag.repository.TagRepository;
import com.buddy.buddy.tag.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagServiceImplementation implements TagService {

    private final TagRepository tagRepository;
    private static final Logger logger = LoggerFactory.getLogger(TagServiceImplementation.class);

    public TagServiceImplementation(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public ResponseEntity<HttpStatus> addTag(AddTagDTO addTagDTO) {
        try {
            Optional<Tag> tags = tagRepository.findByNameContainingIgnoreCase(addTagDTO.getTagName());
            if (tags.isEmpty()) {
                Tag tag = new Tag();
                tag.setName(addTagDTO.getTagName());
                tagRepository.save(tag);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Page<Tag>> getAllTags(Pageable pageable) {
        try{
            return new ResponseEntity<>(tagRepository.findAllOrderByName(pageable), HttpStatus.OK);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<Page<Tag>> getSearchTag(String tagName, Pageable pageable) {
        try {
            return new ResponseEntity<>(tagRepository.findByNameContainingIgnoreCaseToAdd(tagName, pageable), HttpStatus.OK);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> mediaTag(String tagName, User user, Pageable pageable) {
        return null;
    }
}
