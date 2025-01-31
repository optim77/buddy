package com.buddy.buddy.plan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
