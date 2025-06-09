package com.buddy.buddy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthOperationException extends RuntimeException {
    private final HttpStatus status;

    public AuthOperationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
