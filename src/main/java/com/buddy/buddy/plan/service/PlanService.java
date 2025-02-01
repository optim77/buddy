package com.buddy.buddy.plan.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PlanService {
    ResponseEntity<GetPlanDTO> getPlan(UUID planId);
    ResponseEntity<List<GetPlansDTO>> getPlans(User user, Pageable pageable);
    ResponseEntity<HttpStatus> createPlan(CreatePlanDTO createPlanDTO, User user);
    ResponseEntity<HttpStatus> updatePlan(UpdatePlanDTO updatePlanDTO, User user);
    ResponseEntity<HttpStatus> deletePlan(UUID planId, User user);
    ResponseEntity<Page<GetReviewSubscriberDTO>> getPlanSubscribers(UUID plan, User user, Pageable pageable);
}
