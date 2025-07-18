package com.buddy.buddy.follow.servce.implementation;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.follow.entity.Follow;
import com.buddy.buddy.follow.repository.FollowRepository;
import com.buddy.buddy.follow.servce.FollowService;
import com.buddy.buddy.like.service.implementation.LikeServiceImplementation;
import com.buddy.buddy.notification.NotificationType;
import com.buddy.buddy.notification.Service.NotificationProducerService;
import com.buddy.buddy.subscription.entity.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class FollowServiceImplementation implements FollowService {

    private final FollowRepository followRepository;
    private static final Logger logger = LoggerFactory.getLogger(FollowServiceImplementation.class.getName());
    private final UserRepository userRepository;
    private final NotificationProducerService notificationProducer;

    public FollowServiceImplementation(FollowRepository followRepository, UserRepository userRepository, NotificationProducerService notificationProducer) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
    }

    @Override
    public ResponseEntity<HttpStatus> followOrUnfollow(User user, UUID followedTo) {
        if (userRepository.findById(user.getId()).isPresent()){
            try {
                Optional<Follow> isFollowed = followRepository.findByUserAndFollowedTo(user.getId(), followedTo);
                if (isFollowed.isPresent()) {
                    followRepository.deleteByUserAndFollowedTo(user.getId(), followedTo);
                    user.setFollowing(user.getFollowing() - 1);
                    userRepository.save(user);

                    userRepository.findById(followedTo).ifPresent(followed -> {
                        followed.setFollowers(followed.getFollowers() - 1);
                        userRepository.save(followed);
                    });
                }else {
                    Follow follow = new Follow();
                    Optional<User> followedToUser = userRepository.findById(followedTo);
                    if (followedToUser.isPresent()) {
                        follow.setFollowedTo(followedToUser.get());
                        user.setFollowing(user.getFollowing() + 1);
                        userRepository.save(user);
                        followedToUser.get().setFollowers(followedToUser.get().getFollowers() + 1);
                        userRepository.save(followedToUser.get());
                        notificationProducer.sendNotification(
                                followedToUser.get().getUsername(),
                                followedToUser.get().getId(),
                                user.getUsername(),
                                user.getId(),
                                NotificationType.Follow,
                                "",
                                LocalDateTime.now()
                        );
                    }else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                    }
                    follow.setFollower(user);
                    follow.setCreated(LocalDate.now());
                    followRepository.save(follow);
                }
                return new ResponseEntity<>(HttpStatus.OK);

            }catch (Exception e) {
                logger.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

    }

    @Override
    public ResponseEntity<Page<GetUserInformationDTO>> getFollowers(User user, Pageable pageable) {
        try {
            Page<GetUserInformationDTO> getUserInformationDTOS = followRepository.findFollowersForUser(user.getId(), pageable);
            return new ResponseEntity<>(getUserInformationDTOS, HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<Page<GetUserInformationDTO>> getFollowing(User user, Pageable pageable) {
        try {
            Page<GetUserInformationDTO> getUserInformationDTOS = followRepository.findFollowingForUser(user.getId(), pageable);
            return new ResponseEntity<>(getUserInformationDTOS, HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
