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
public class CreatePlanDTO {

    private String name;
    private String description;
    private int price;
}
