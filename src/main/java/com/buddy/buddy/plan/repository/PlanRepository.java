package com.buddy.buddy.plan.repository;

import com.buddy.buddy.plan.DTO.GetPlanDTO;
import com.buddy.buddy.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {

    @Query("SELECT new com.buddy.buddy.plan.DTO.GetPlanDTO(p.id, p.name, p.description, p.price, p.user.id, p.user.username, p.subscriptions) FROM Plan p where p.id = :plan_id")
    Optional<GetPlanDTO> getPlanById(@Param("plan_id") UUID id);
}
