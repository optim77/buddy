package com.buddy.buddy.image.DTO;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.entity.MediaType;
import com.buddy.buddy.like.entity.Like;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class GetImageDTO {

    public GetImageDTO(Image image, User user) {
        this.id = image.getId();
        this.description = image.getDescription();
        this.likes = image.getLikeCount();
        this.getUserInformationDTO = new GetUserInformationDTO(user);
    }

    private UUID id;
    private String description;
    private int likes;
    private String url;
    private GetUserInformationDTO getUserInformationDTO;
    private boolean liked = false;
    private MediaType mediaType;
}
