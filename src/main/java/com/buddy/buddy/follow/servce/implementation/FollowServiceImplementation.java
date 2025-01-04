package com.buddy.buddy.follow.servce.implementation;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.follow.entity.Follow;
import com.buddy.buddy.follow.repository.FollowRepository;
import com.buddy.buddy.follow.servce.FollowService;
import com.buddy.buddy.like.service.implementation.LikeServiceImplementation;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class FollowServiceImplementation implements FollowService {

    private final FollowRepository followRepository;
    private static final Logger logger = LoggerFactory.getLogger(FollowServiceImplementation.class.getName());
    private final UserRepository userRepository;

    public FollowServiceImplementation(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
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
    public ResponseEntity<Page<GetUserInformationDTO>> follows(User user, Pageable pageable) {
        try {
            Page<GetUserInformationDTO> getUserInformationDTOS = followRepository.findFollowedByUser(user.getId(), pageable);
            return new ResponseEntity<>(getUserInformationDTOS, HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }
}
