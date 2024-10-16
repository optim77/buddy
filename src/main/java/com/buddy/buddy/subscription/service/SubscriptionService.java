package com.buddy.buddy.subscription.service;

import com.buddy.buddy.subscription.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface SubscriptionService {
    ResponseEntity<Page<Subscription>> getAllSubscriptions(Pageable pageable);
}
