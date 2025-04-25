package com.buddy.buddy.plan.repository;

import com.buddy.buddy.plan.DTO.GetPlanDTO;
import com.buddy.buddy.plan.DTO.GetPlansDTO;
import com.buddy.buddy.plan.DTO.GetReviewSubscriberDTO;
import com.buddy.buddy.plan.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {

    @Query("SELECT new com.buddy.buddy.plan.DTO.GetPlanDTO(p.id, p.name, p.description, p.price, p.user.id, p.user.username, p.subscriptionsCount) FROM Plan p WHERE p.id = :plan_id")
    Optional<GetPlanDTO> getPlanById(@Param("plan_id") UUID id);

    @Query("SELECT new com.buddy.buddy.plan.DTO.GetPlansDTO(p.id, p.name, p.description, p.price, p.user.id, p.user.username, p.subscriptionsCount) FROM Plan p WHERE p.user.id = :user_id")
    List<GetPlansDTO> getUserPlans(@Param("user_id") UUID user_id);

    @Query("SELECT new com.buddy.buddy.plan.DTO.GetReviewSubscriberDTO(s.subscriber.id, s.subscriber.username, s.subscriber.avatar, s.plan.name, s.subscriber.createdAt) " +
            "FROM Subscription s " +
            "WHERE s.plan.id = :plan " +
            "AND s.subscribedTo.id = :subscriberTo")
    Page<GetReviewSubscriberDTO> getPlanSubscribers(@Param("plan") UUID plan, @Param("subscriberTo") UUID subscriberTo, Pageable pageable);

    @Query("SELECT COUNT(*) FROM Plan p WHERE p.user.id = :user_id")
    int countPlanByUserId(@Param("user_id") UUID user_id);
}