package com.buddy.buddy.plan.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.*;
import com.buddy.buddy.plan.entity.Plan;
import com.buddy.buddy.plan.repository.PlanRepository;
import com.buddy.buddy.plan.service.PlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanServiceImplementation implements PlanService {
    private final PlanRepository planRepository;
    public PlanServiceImplementation(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public ResponseEntity<GetPlanDTO> getPlan(UUID planId) {
        Optional<GetPlanDTO> getPlanDTO = planRepository.getPlanById(planId);
        return getPlanDTO.map(planDTO -> new ResponseEntity<>(planDTO, HttpStatus.OK)).orElseThrow();
    }

    @Override
    public ResponseEntity<List<GetPlansDTO>> getPlans(User user, Pageable pageable) {
        try {
            List<GetPlansDTO> plansDTOS = planRepository.getUserPlans(user.getId());
            return new ResponseEntity<>(plansDTOS, HttpStatus.OK);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<HttpStatus> createPlan(CreatePlanDTO createPlanDTO, User user) {
        try {
            if (planRepository.countPlanByUserId(user.getId()) >= 5) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            Plan plan = new Plan();
            plan.setName(createPlanDTO.getName());
            plan.setDescription(createPlanDTO.getDescription());
            plan.setUser(user);
            plan.setPrice(createPlanDTO.getPrice());
            planRepository.save(plan);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<HttpStatus> updatePlan(UpdatePlanDTO updatePlanDTO, User user) {
        try {
            planRepository.findById(updatePlanDTO.getId()).ifPresent(existingPlan -> {
                if(existingPlan.getUser().getId().equals(user.getId())){
                    if (!existingPlan.getDescription().equals(updatePlanDTO.getDescription())){
                        existingPlan.setDescription(updatePlanDTO.getDescription());
                    }
                    if (!existingPlan.getName().equals(updatePlanDTO.getName())){
                        existingPlan.setName(updatePlanDTO.getName());
                    }
                    if (existingPlan.getPrice() != updatePlanDTO.getPrice()){
                        existingPlan.setPrice(updatePlanDTO.getPrice());
                    }
                    planRepository.save(existingPlan);
                }

            });
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deletePlan(UUID planId, User user) {
        Optional<Plan> plan = planRepository.findById(planId);
        if (plan.isPresent() && plan.get().getUser().equals(user)) {
            planRepository.deleteById(planId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Page<GetReviewSubscriberDTO>> getPlanSubscribers(UUID plan, User user, Pageable pageable) {
        try {
            Page<GetReviewSubscriberDTO> dto = planRepository.getPlanSubscribers(plan, user.getId(), pageable);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
