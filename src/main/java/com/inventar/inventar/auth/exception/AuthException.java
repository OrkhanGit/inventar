package com.inventar.inventar.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatus status;
    private final Integer remainingAttempts;
    private final Long retryAfterSeconds;

    public AuthException(HttpStatus status, String message, Integer remainingAttempts, Long retryAfterSeconds) {
        super(message);
        this.status = status;
        this.remainingAttempts = remainingAttempts;
        this.retryAfterSeconds = retryAfterSeconds;
    }
}