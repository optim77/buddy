package com.buddy.buddy.account.DTO;

import com.buddy.buddy.account.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInformationDTO {


    private String username;
    private String password;
    private String description;
    private String avatar;
    private boolean deleted;
    private boolean active;
}
