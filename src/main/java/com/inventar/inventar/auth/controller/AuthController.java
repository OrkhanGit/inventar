package com.inventar.inventar.auth.controller;

import com.inventar.inventar.auth.exception.AuthException;
import com.inventar.inventar.auth.model.AuthErrorResponse;
import com.inventar.inventar.auth.model.LoginRequest;
import com.inventar.inventar.auth.model.RefreshRequest;
import com.inventar.inventar.auth.model.TokenResponse;
import com.inventar.inventar.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthErrorResponse> handle(AuthException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new AuthErrorResponse(e.getMessage(), e.getRemainingAttempts(), e.getRetryAfterSeconds()));
    }
}