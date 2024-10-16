package com.buddy.buddy.subscription.service.implementation;

import com.buddy.buddy.subscription.entity.Subscription;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import com.buddy.buddy.subscription.service.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public class SubscriptionServiceImplementation  implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImplementation(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public ResponseEntity<Page<Subscription>> getAllSubscriptions(Pageable pageable) {
        return null;
    }
}
