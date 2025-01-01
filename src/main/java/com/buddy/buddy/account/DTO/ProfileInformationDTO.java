package com.buddy.buddy.account.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
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
}
