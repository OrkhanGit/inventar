package com.inventar.inventar.auth.model;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}