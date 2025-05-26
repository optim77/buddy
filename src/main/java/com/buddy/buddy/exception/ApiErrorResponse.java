package com.buddy.buddy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private int code;
    private Instant timestamp;


}
