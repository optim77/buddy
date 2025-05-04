package com.buddy.buddy.subscription.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.subscription.DTO.GetUserInformationSubscriptionDTO;
import com.buddy.buddy.subscription.entity.Subscription;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import com.buddy.buddy.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionServiceImplementation  implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImplementation.class.getName());
    private final UserRepository userRepository;

    public SubscriptionServiceImplementation(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<HttpStatus> subscribe(User user, UUID subscriptionTo) {
        try {
            Subscription subscription = new Subscription();
            subscription.setSubscriber(user);
            Optional<User> subscribeTo = userRepository.findById(subscriptionTo);

            if (subscribeTo.isPresent()) {
                subscription.setSubscriber(subscribeTo.get());
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            LocalDate startDate = LocalDate.now();
            subscription.setStartDate(startDate);

            LocalDate endDate = startDate.plusDays(30);
            subscription.setEndDate(endDate);

            subscriptionRepository.save(subscription);
            logger.debug("Create new subscription {}", subscription.getId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<Page<GetUserInformationSubscriptionDTO>> getUserSubscriptions(User user, Pageable pageable) {
        try {
            Page<GetUserInformationSubscriptionDTO> userInformationSubscriptionDTOS = subscriptionRepository.getSubscriptionToUserByUserId(user.getId(), pageable);
            logger.debug("Return user's subscriptions");
            return ResponseEntity.ok(userInformationSubscriptionDTOS);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }

    }

    @Override
    public ResponseEntity<Subscription> cancelSubscription(User user, UUID subscription_id) {
        try {
            Optional<Subscription> subscription = subscriptionRepository.findById(subscription_id);
            if (subscription.isPresent()) {
                subscription.get().setCancelled(true);
            }else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found");
            }
            subscriptionRepository.save(subscription.get());
            return new ResponseEntity<>(subscription.get(), HttpStatus.OK);

        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
