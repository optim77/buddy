package com.buddy.buddy.subscription.service;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.subscription.DTO.GetUserInformationSubscriptionDTO;
import com.buddy.buddy.subscription.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

public interface SubscriptionService {

    ResponseEntity<HttpStatus> subscribe(User user, UUID subscriptionTo);
    ResponseEntity<Page<GetUserInformationSubscriptionDTO>> getUserSubscriptions(User user, Pageable pageable);
    ResponseEntity<Subscription> cancelSubscription(@AuthenticationPrincipal User user, UUID subscriptionTo);

}
