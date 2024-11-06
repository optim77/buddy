package com.buddy.buddy.follow.servce;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FollowService {

    ResponseEntity<HttpStatus> followOrUnfollow(User user, UUID followedTo);

    ResponseEntity<Page<GetUserInformationDTO>> follows(User user, Pageable pageable);
}
