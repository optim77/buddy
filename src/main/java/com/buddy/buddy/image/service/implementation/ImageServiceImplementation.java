package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.ImageService;
import com.buddy.buddy.subscription.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class ImageServiceImplementation implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ImageServiceImplementation(ImageRepository imageRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public ResponseEntity<GetImageDTO> getImage(UUID imageId) {
        return imageRepository.findById(imageId).map(image -> {
            Optional<User> user = Optional.of(userRepository.findById(image.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
            GetImageDTO dto = new GetImageDTO(image, user.get());
            if (image.isOpen()) {
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                if (isSubscriber(image.getAccount().getId())) {
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                }
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            }
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }

    private boolean isSubscriber(UUID subscriberTo) {
        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            User loggedUser = userRepository.findByUsernameOrEmail(username, username);

            return subscriptionRepository.existsBySubscriberAndSubscribedTo(loggedUser.getId(), subscriberTo);

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
