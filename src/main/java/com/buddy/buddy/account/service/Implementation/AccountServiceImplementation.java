package com.buddy.buddy.account.service.Implementation;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.GetUserProfileInformationDTO;
import com.buddy.buddy.account.DTO.ProfileInformationDTO;
import com.buddy.buddy.account.DTO.UpdateUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.service.AccountService;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.implementation.ImageServiceHelper;
import com.buddy.buddy.image.service.implementation.ImageServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${app.file.storage-path}")
    private String storagePath;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImplementation.class);

    public AccountServiceImplementation(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<GetUserProfileInformationDTO> getAccount(UUID userId, User user) {
        try {
            if (user != null) {
                GetUserProfileInformationDTO dto = userRepository.findGetUserProfileInformationByIdForLogged(userId, user.getId());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            GetUserProfileInformationDTO dto = userRepository.findGetUserProfileInformationById(userId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

//        return userRepository.findById(userId).map(user -> {
//            if (user.isLocked() || user.isDeleted() || !user.isActive()) {
//                logger.debug("Cannot get user - is locked");
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find user");
//            }
//            GetUserProfileInforamationDTO getUserDTO = new GetUserProfileInforamationDTO(user);
//            getUserDTO.setUuid(user.getId());
//            logger.debug("Get user DTO: {}", getUserDTO);
//            return ResponseEntity.ok(getUserDTO);
//        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
        Optional<User> fetchedUser = userRepository.findById(user.getId());
        if (fetchedUser.isPresent()) {
            try {
                if (updateUserInformationDTO.getUsername() != null && !updateUserInformationDTO.getUsername().isEmpty() && !updateUserInformationDTO.getUsername().equals(fetchedUser.get().getUsername())) {
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
                if (updateUserInformationDTO.getDescription() != null && !updateUserInformationDTO.getDescription().isEmpty() && !updateUserInformationDTO.getDescription().equals(fetchedUser.get().getDescription())) {
                    user.setDescription(updateUserInformationDTO.getDescription());
                    getUserDTO.setDescription(updateUserInformationDTO.getDescription());
                }
                user.setDeleted(updateUserInformationDTO.isDeleted());

                userRepository.save(user);
                return ResponseEntity.ok().body(HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Error while updating user", e);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Error while updating user");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public ResponseEntity<HttpStatus> updatePassword(String password, User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        try {
            if (isValidPassword(password)) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                return ResponseEntity.ok(HttpStatus.OK);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password does not meet the requirements (8-32 characters, upper and lower case, special character)");

        }catch (Exception e){
            logger.error("Error while updating password", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error while updating password");
        }
    }

    @Override
    public ResponseEntity<HttpStatus> changeAvatar(MultipartFile file, User user) {
        try {
            ImageServiceHelper.validateFile(file);

            Path uploadPath = Paths.get(storagePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            UUID randomUUID = UUID.randomUUID();
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase();
            String savedFileName = randomUUID.toString() + "." + fileExtension;
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            try {
                File oldAvatar = new File(storagePath + user.getAvatar());
                oldAvatar.delete();
            }catch (Exception e){
                logger.error("Error while deleting old avatar", e);
            }

            user.setAvatar(savedFileName);
            userRepository.save(user);

            return ResponseEntity.ok(HttpStatus.OK);

        }catch (Exception e){
            logger.error("Error while changing avatar", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error while changing avatar");
        }
    }

    @Override
    public ResponseEntity<HttpStatus> lockAccount(User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        try {
            user.setLocked(!user.isLocked());
            userRepository.save(user);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch (Exception e){
            logger.error("Error while updating password", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error while updating password");
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deleteUser(User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        try {
            user.setDeleted(true);
            userRepository.save(user);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";
        return password == null || !password.matches(passwordRegex);
    }
}
