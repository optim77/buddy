package com.buddy.buddy.image.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageWithUserLikeDTO   {
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
    private boolean likedByCurrentUser = false;
}
