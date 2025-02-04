package com.buddy.buddy.account.DTO;

import com.buddy.buddy.plan.DTO.GetPlansDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInformationDTO {

    private UUID uuid;
    private String email;
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
    private int subscribed;

    private List<GetPlansDTO> plans;

    public ProfileInformationDTO(UUID uuid, String email, String username, String description,
                                 int age, String avatar,boolean active,
                                 boolean locked, int posts, int followers, int following, int subscribers,
                                 int subscribed) {
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.description = description;
        this.age = age;
        this.avatar = avatar;
        this.active = active;
        this.locked = locked;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.subscribers = subscribers;
        this.subscribed = subscribed;

    }

}
