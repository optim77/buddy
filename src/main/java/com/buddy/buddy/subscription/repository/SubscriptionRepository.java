package com.buddy.buddy.subscription.repository;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.subscription.DTO.GetUserInformationSubscriptionDTO;
import com.buddy.buddy.subscription.entity.Subscription;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

//    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
//            "FROM Subscription s WHERE s.subscriber.id = :subscriberId AND s.subscribedTo.id = :subscribedToId")
    @Query("SELECT s.active FROM Subscription s WHERE s.subscriber.id = :subscriberId AND s.subscribedTo.id = :subscribedToId")
    boolean existsBySubscriberAndSubscribedTo(@Param("subscriberId") UUID subscriberId,
                                              @Param("subscribedToId") UUID subscribedToId);

    @Query("SELECT new com.buddy.buddy.subscription.DTO.GetUserInformationSubscriptionDTO(s.subscribedTo.id, " +
            "s.subscribedTo.username, s.subscribedTo.description, s.subscribedTo.age, s.subscribedTo.avatar, " +
            "s.subscribedTo.createdAt, s.subscribedTo.active, s.subscribedTo.deleted, s.subscribedTo.locked) " +
            "FROM Subscription s where s.subscriber = :user_id")
    Page<GetUserInformationSubscriptionDTO> getSubscriptionToUserByUserId(@Param("user_id") UUID userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.cancelled = true WHERE s.subscriber = :user_id AND s.id = :subscription_id")
    void cancelSubscription(@Param("user_id") UUID user_id, @Param("subscription_id") UUID subscription_id);

    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.cancelled = true AND s.active = true")
    List<Subscription> findExpiredAndCancelledSubscriptions(LocalDate currentDate);

    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.cancelled = false")
    List<Subscription> findSubscriptionToRenewal(LocalDate currentDate);

}