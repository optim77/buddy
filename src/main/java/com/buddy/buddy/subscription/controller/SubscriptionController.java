package com.buddy.buddy.subscription.controller;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.subscription.DTO.GetUserInformationSubscriptionDTO;
import com.buddy.buddy.subscription.entity.Subscription;
import com.buddy.buddy.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Repository
@RequestMapping(produces = "application/json")
public class SubscriptionController {

    @Autowired
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    //one more service for payment before subscription

    @PostMapping("/subscription/new")
    private ResponseEntity<HttpStatus> getSubscribe(@AuthenticationPrincipal User user, UUID subscriptionTo){
        return subscriptionService.subscribe(user, subscriptionTo);
    }

    @GetMapping("/subscriptions")
    private ResponseEntity<Page<GetUserInformationSubscriptionDTO>> getSubscriptions(@AuthenticationPrincipal User user, Pageable pageable) {
        return subscriptionService.getUserSubscriptions(user, pageable);
    }

    //cancel subscription
    @PostMapping("subscription/cancel")
    private ResponseEntity<Subscription> cancelSubscription(@AuthenticationPrincipal User user, UUID subscription_id) {
        return subscriptionService.cancelSubscription(user, subscription_id);
    }

    //auto renewal - cron service

}
