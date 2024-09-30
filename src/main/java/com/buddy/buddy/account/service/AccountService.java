package com.buddy.buddy.account.service;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AccountService {
    ResponseEntity<User> getAccount(UUID uuid);
}
