package com.buddy.buddy.account.service.Implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<User> getAccount(UUID uuid) {
        return null;
    }
}
