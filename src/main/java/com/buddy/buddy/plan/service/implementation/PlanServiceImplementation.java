package com.buddy.buddy.plan.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.DTO.CreatePlanDTO;
import com.buddy.buddy.plan.DTO.GetPlanDTO;
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
        Optional<Plan> plan = planRepository.findById(planId);
        if (plan.isPresent() && plan.get().getUser().equals(user)) {
            planRepository.deleteById(planId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
