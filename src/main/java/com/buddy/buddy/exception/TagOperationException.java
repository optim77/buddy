package com.buddy.buddy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TagOperationException extends RuntimeException {
    private final HttpStatus status;

    public TagOperationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
