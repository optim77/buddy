package com.buddy.buddy.image.DTO;

import com.buddy.buddy.image.entity.MediaType;
import com.buddy.buddy.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageWithUserLikeAndTagsDTO {

    private UUID imageId;
    private String imageUrl;
    private String description;
    private Date uploadedDate;
    private int likeCount;
    private boolean open;

    private UUID userId;
    private String username;
    private String avatar;
    private Date userCreatedAt;
    private Set<String> tags;
    private boolean likedByCurrentUser;
    private MediaType mediaType;

    public ImageWithUserLikeAndTagsDTO(ImageWithUserLikeDTO imageWithUserLikeDTO, Set<String> tags) {
        this.imageId = imageWithUserLikeDTO.getImageId();
        this.imageUrl = imageWithUserLikeDTO.getImageUrl();
        this.description = imageWithUserLikeDTO.getDescription();
        this.uploadedDate = imageWithUserLikeDTO.getUploadedDate();
        this.likeCount = imageWithUserLikeDTO.getLikeCount();
        this.open = imageWithUserLikeDTO.isOpen();
        this.userId = imageWithUserLikeDTO.getUserId();
        this.username = imageWithUserLikeDTO.getUsername();
        this.avatar = imageWithUserLikeDTO.getAvatar();
        this.userCreatedAt = imageWithUserLikeDTO.getUserCreatedAt();
        this.mediaType = imageWithUserLikeDTO.getMediaType();
        this.likedByCurrentUser = imageWithUserLikeDTO.isLikedByCurrentUser();
        this.tags = tags;
    }




}
