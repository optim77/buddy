package com.buddy.buddy.plan.DTO;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.subscription.entity.Subscription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPlanDTO {

    private UUID id;
    private String name;
    private String description;
    private int price;
    private UUID planOwnerId;
    private String planOwnerName;
    private Set<Subscription> subscriptions;
}
