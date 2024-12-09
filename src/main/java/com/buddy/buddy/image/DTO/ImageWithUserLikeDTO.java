package com.buddy.buddy.image.DTO;

import com.buddy.buddy.image.entity.MediaType;
import com.buddy.buddy.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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
    private MediaType mediaType;
    private boolean likedByCurrentUser;





}
