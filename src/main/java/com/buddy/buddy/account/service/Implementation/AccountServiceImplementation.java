package com.buddy.buddy.account.service.Implementation;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.ProfileInformationDTO;
import com.buddy.buddy.account.DTO.UpdateUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.service.AccountService;
import com.buddy.buddy.auth.AuthenticationService;
import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImplementation.class);

    @Override
    public ResponseEntity<GetUserInformationDTO> getAccount(UUID userId) {

        return userRepository.findById(userId).map(user -> {
            if (user.isLocked() || user.isDeleted() || !user.isActive()) {
                logger.debug("Cannot get user - is locked");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user");
            }
            GetUserInformationDTO getUserDTO = new GetUserInformationDTO(user);
            getUserDTO.setId(user.getId());
            logger.debug("Get user DTO: {}", getUserDTO);
            return ResponseEntity.ok(getUserDTO);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public ResponseEntity<Page<GetUserInformationDTO>> getUserListRandom(Pageable pageable) {
        logger.debug("Getting random user list");
        Page<GetUserInformationDTO> users = userRepository.findAllRandom(pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<Page<GetUserInformationDTO>> getUserListByCriteria(String criteria, Pageable pageable) {
        if (criteria.equals("popularity")) {
            logger.debug("Getting user list by popularity");
            Page<GetUserInformationDTO> users = userRepository.findAllByPopularity(pageable);
            return ResponseEntity.ok(users);
        }
        if (criteria.equals("newest")) {
            logger.debug("Getting user list by newest");
            Page<GetUserInformationDTO> users = userRepository.findAllByCreatedAt(pageable);
            return ResponseEntity.ok(users);
        }
        logger.debug("Insert wrong criteria");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong criteria provided");
    }

    @Override
    public ResponseEntity<ProfileInformationDTO> getProfileInformation(UUID uuid) {
        try {
            return new ResponseEntity<>(userRepository.findProfileInformationById(uuid), HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    public ResponseEntity<Page<ImageWithUserLikeDTO>> profilePhotos(UUID uuid, Pageable pageable) {
        try {
            Page<ImageWithUserLikeDTO> images = imageRepository.findImagesByUserIdWithUserAndLikeStatus(uuid, uuid, pageable);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    public ResponseEntity<Page<GetUserInformationDTO>> searchUser(String search, Pageable pageable) {
        Page<User> users = userRepository.findByUsernameContainingIgnoreCaseOrderBySubscribersCount(search, pageable);
        if (users == null || users.isEmpty()) {
            logger.debug("There is no users");
            return ResponseEntity.ok(Page.empty(pageable));
        }
        logger.debug("Returning result: {}", users);
        Page<GetUserInformationDTO> userDTOs = users.map(user -> {
            GetUserInformationDTO getUserDTO = new GetUserInformationDTO(user);
            getUserDTO.setId(user.getId());
            return getUserDTO;
        });
        return ResponseEntity.ok(userDTOs);
    }

    @Override
    public ResponseEntity<HttpStatus> updateUser(UpdateUserInformationDTO updateUserInformationDTO, User user) {
        logger.debug("Updating user {}", user.getId());
        GetUserInformationDTO getUserDTO = new GetUserInformationDTO(user);
        try {
            if (updateUserInformationDTO.getUsername() != null && !updateUserInformationDTO.getUsername().isEmpty()) {
                boolean isExistUser = userRepository.existsByUsername(updateUserInformationDTO.getUsername());
                if (isExistUser) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                }
                user.setUsername(updateUserInformationDTO.getUsername());
                getUserDTO.setUsername(updateUserInformationDTO.getUsername());
            }
            if (updateUserInformationDTO.getPassword() != null && !updateUserInformationDTO.getPassword().isEmpty()) {
                user.setPassword(updateUserInformationDTO.getPassword());
            }
            if (updateUserInformationDTO.getDescription() != null && !updateUserInformationDTO.getDescription().isEmpty()) {
                user.setDescription(updateUserInformationDTO.getDescription());
                getUserDTO.setDescription(updateUserInformationDTO.getDescription());
            }
            user.setDeleted(updateUserInformationDTO.isDeleted());
            user.setActive(updateUserInformationDTO.isActive());

            userRepository.save(user);
            return ResponseEntity.ok().body(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating user", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error while updating user");
        }
    }
}
