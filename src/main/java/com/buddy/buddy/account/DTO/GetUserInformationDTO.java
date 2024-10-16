package com.buddy.buddy.account.DTO;

import com.buddy.buddy.account.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter

public class GetUserInformationDTO {

    public GetUserInformationDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.description = user.getDescription();
        this.age = user.getAge();
        this.avatar = user.getAvatar();
        this.createdAt = user.getCreatedAt();
    }

    private UUID id;
    private String username;
    private String description;
    private int age;
    private String avatar;
    private Date createdAt;

}
