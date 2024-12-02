package com.buddy.buddy.account.service;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.ProfileInformationDTO;
import com.buddy.buddy.account.DTO.UpdateUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.DTO.ImageWithUserLikeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AccountService {
    ResponseEntity<GetUserInformationDTO> getAccount(UUID uuid);
    ResponseEntity<Page<GetUserInformationDTO>> searchUser(String username, Pageable pageable);
    ResponseEntity<HttpStatus> updateUser(UpdateUserInformationDTO updateUserInformationDTO, User user);
    ResponseEntity<Page<GetUserInformationDTO>> getUserListRandom(Pageable pageable);
    ResponseEntity<Page<GetUserInformationDTO>> getUserListByCriteria(String criteria, Pageable pageable);
    ResponseEntity<ProfileInformationDTO> getProfileInformation(UUID uuid);
    ResponseEntity<Page<ImageWithUserLikeDTO>> profilePhotos(UUID uuid, Pageable pageable);
}
