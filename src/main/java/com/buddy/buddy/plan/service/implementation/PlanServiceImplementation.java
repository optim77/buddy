package com.buddy.buddy.plan.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.CreatePlanDTO;
import com.buddy.buddy.plan.DTO.UpdatePlanDTO;
import com.buddy.buddy.plan.entity.Plan;
import com.buddy.buddy.plan.service.PlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlanServiceImplementation implements PlanService {
    @Override
    public ResponseEntity<Plan> getPlan(UUID planId, User user) {
        return null;
    }

    @Override
    public ResponseEntity<Page<Plan>> getPlans(User user, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> createPlan(CreatePlanDTO createPlanDTO, User user) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> updatePlan(UpdatePlanDTO updatePlanDTO, User user) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deletePlan(UUID planId, User user) {
        return null;
    }
}
