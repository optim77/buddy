package com.buddy.buddy.subscription.repository;

import com.buddy.buddy.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subscription s WHERE s.subscriber.id = :subscriberId AND s.subscribedTo.id = :subscribedToId")
    boolean existsBySubscriberAndSubscribedTo(@Param("subscriberId") UUID subscriberId,
                                              @Param("subscribedToId") UUID subscribedToId);
}
