package com.buddy.buddy.plan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePlanDTO extends CreatePlanDTO{

    private UUID id;

}
