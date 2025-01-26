package com.buddy.buddy.plan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlanDTO {

    private String name;
    private String description;
    private int price;
}
