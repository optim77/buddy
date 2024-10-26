package com.buddy.buddy.image.DTO;

import com.buddy.buddy.tag.entity.Tag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UploadImageDTO {
    private String description;
    private boolean open;
    private MultipartFile file;
    private Set<String> tagSet;
}
