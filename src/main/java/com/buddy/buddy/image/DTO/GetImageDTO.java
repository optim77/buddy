package com.buddy.buddy.image.DTO;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.entity.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class GetImageDTO {

    public GetImageDTO(Image image, User user) {
        this.id = image.getId();
        this.publishedDate = image.getPublishedDate();
        this.description = image.getDescription();
        this.likes = image.getLikeCount();
        this.getUserInformationDTO = new GetUserInformationDTO(user);
    }

    private UUID id;
    private Date publishedDate;
    private String description;
    private int likes;
    private GetUserInformationDTO getUserInformationDTO;
}
