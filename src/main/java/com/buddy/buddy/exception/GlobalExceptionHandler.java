package com.buddy.buddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthOperationException.class)
    public ResponseEntity<ApiErrorResponse> authOperationException(AuthOperationException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(e.getMessage(), e.getStatus().value(), Instant.now());
        return new ResponseEntity<>(apiErrorResponse, e.getStatus());
    }


    @ExceptionHandler(AccountOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountOperationException(AccountOperationException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(e.getMessage(), e.getStatus().value(), Instant.now());
        return new ResponseEntity<>(apiErrorResponse, e.getStatus());
    }

    @ExceptionHandler(SessionOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionException(SessionOperationException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(e.getMessage(), e.getStatus().value(), Instant.now());
        return new ResponseEntity<>(apiErrorResponse, e.getStatus());
    }

    @ExceptionHandler(TagOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleTagOperationException(TagOperationException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(e.getMessage(), e.getStatus().value(), Instant.now());
        return new ResponseEntity<>(apiErrorResponse, e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
