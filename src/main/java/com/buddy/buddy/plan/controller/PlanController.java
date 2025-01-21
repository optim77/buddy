package com.buddy.buddy.plan.controller;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.CreatePlanDTO;
import com.buddy.buddy.plan.DTO.UpdatePlanDTO;
import com.buddy.buddy.plan.entity.Plan;
import com.buddy.buddy.plan.repository.PlanRepository;
import com.buddy.buddy.plan.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(produces = "application/json")
public class PlanController {

    @Autowired
    private final PlanRepository planRepository;

    @Autowired
    private final PlanService planService;

    public PlanController(PlanRepository planRepository, PlanService planService) {
        this.planRepository = planRepository;
        this.planService = planService;
    }

    @GetMapping("/plan/{plan}")
    public ResponseEntity<Plan> getPlan(@PathVariable UUID plan, @AuthenticationPrincipal User user) {
        return planService.getPlan(plan, user);
    }

    @GetMapping("/plan/all")
    private ResponseEntity<Page<Plan>> getPlans(@AuthenticationPrincipal User user, Pageable pageable) {
        return planService.getPlans(user, pageable);
    }

    @PostMapping("/plan/create")
    private ResponseEntity<HttpStatus> createPlan(@RequestBody CreatePlanDTO createPlanDTO, @AuthenticationPrincipal User user) {
        return planService.createPlan(createPlanDTO, user);
    }
    @PutMapping("/plan/update")
    private ResponseEntity<HttpStatus> updatePlan(@RequestBody UpdatePlanDTO plan, @AuthenticationPrincipal User user) {
        return planService.updatePlan(plan, user);
    }

    @DeleteMapping("/plan/delete/{id}")
    private ResponseEntity<HttpStatus> deletePlan(@PathVariable UUID id, @AuthenticationPrincipal User user){
        return planService.deletePlan(id, user);
    }
}
