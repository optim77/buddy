package com.buddy.buddy.subscription.DTO;

import com.buddy.buddy.account.entity.User;
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
public class GetUserInformationSubscriptionDTO {

    private UUID id;
    private String username;
    private String description;
    private int age;
    private String avatar;
    private Date createdAt;
    private boolean isActive;
    private boolean deleted;
    private boolean locked;

}
