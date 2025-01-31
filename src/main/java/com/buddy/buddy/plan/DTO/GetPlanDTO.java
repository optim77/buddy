package com.buddy.buddy.plan.DTO;

import com.buddy.buddy.subscription.entity.Subscription;
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
    private int subscriptionsCount;
}
