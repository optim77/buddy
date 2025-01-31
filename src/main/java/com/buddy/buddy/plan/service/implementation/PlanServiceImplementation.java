package com.buddy.buddy.plan.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.CreatePlanDTO;
import com.buddy.buddy.plan.DTO.GetPlanDTO;
import com.buddy.buddy.plan.DTO.GetPlansDTO;
import com.buddy.buddy.plan.DTO.UpdatePlanDTO;
import com.buddy.buddy.plan.entity.Plan;
import com.buddy.buddy.plan.repository.PlanRepository;
import com.buddy.buddy.plan.service.PlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Page<GetPlansDTO>> getPlans(User user, Pageable pageable) {
        try {
            Page<GetPlansDTO> plansDTOS = planRepository.getUserPlans(user.getId(), pageable);
            return new ResponseEntity<>(plansDTOS, HttpStatus.OK);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<HttpStatus> createPlan(CreatePlanDTO createPlanDTO, User user) {
        try {
            Plan plan = new Plan();
            plan.setName(createPlanDTO.getName());
            plan.setDescription(createPlanDTO.getDescription());
            plan.setUser(user);
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
}
