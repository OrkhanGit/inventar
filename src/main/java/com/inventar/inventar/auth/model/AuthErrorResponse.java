package com.inventar.inventar.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthErrorResponse {
    private String message;
    private Integer remainingAttempts;   // yalnız səhv parol (401) cavabında
    private Long retryAfterSeconds;      // yalnız blok (423) cavabında
}