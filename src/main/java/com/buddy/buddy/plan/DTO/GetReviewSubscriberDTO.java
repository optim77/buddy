package com.buddy.buddy.plan.DTO;

import com.buddy.buddy.plan.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GetReviewSubscriberDTO {
    private UUID userId;
    private String username;
    private String avatar;
    private String plan;
    private Date subscribedAt;
}
