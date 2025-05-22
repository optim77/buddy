package com.buddy.buddy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountOperationException  extends RuntimeException {
    private final HttpStatus status;

    public AccountOperationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
