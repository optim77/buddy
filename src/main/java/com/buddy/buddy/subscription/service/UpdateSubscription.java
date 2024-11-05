package com.buddy.buddy.subscription.service;

import com.buddy.buddy.subscription.entity.Subscription;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class UpdateSubscription {

    private final SubscriptionRepository subscriptionRepository;

    public UpdateSubscription(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSubscription() {
        LocalDate currentDate = LocalDate.now();
        List<Subscription> subscriptionsToDeactivate = subscriptionRepository.findExpiredAndCancelledSubscriptions(currentDate);
        for (Subscription subscription : subscriptionsToDeactivate) {
            subscription.setActive(false);
        }
        subscriptionRepository.saveAll(subscriptionsToDeactivate);

    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void renewalSubscription() {
        LocalDate currentDate = LocalDate.now();
        List<Subscription> subscriptionsToRenewal = subscriptionRepository.findSubscriptionToRenewal(currentDate);
        for (Subscription subscription : subscriptionsToRenewal) {
            // payment
            // set new endDate
            // return success
        }
    }
}
