package com.buddy.buddy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SessionOperationException extends RuntimeException {
    private final HttpStatus status;
    public SessionOperationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
