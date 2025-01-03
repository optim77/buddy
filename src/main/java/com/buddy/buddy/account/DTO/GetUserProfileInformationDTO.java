package com.buddy.buddy.account.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetUserProfileInformationDTO {

    private UUID uuid;
    private String username;
    private String description;
    private int age;
    private String avatar;
    private boolean active;
    private boolean locked;
    private int posts;
    private int followers;
    private int following;
    private int subscribers;
    private boolean isFollowed;
    private boolean isSubscribed;

}
