package com.buddy.buddy.image.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateImageDTO {
    private String description;
    private boolean open;
    private List<String> tagSet = new ArrayList<>();
}
