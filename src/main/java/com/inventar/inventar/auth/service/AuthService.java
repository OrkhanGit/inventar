package com.inventar.inventar.auth.service;

import com.inventar.inventar.auth.exception.AuthException;
import com.inventar.inventar.auth.model.AppUser;
import com.inventar.inventar.auth.model.LoginRequest;
import com.inventar.inventar.auth.model.RefreshToken;
import com.inventar.inventar.auth.model.TokenResponse;
import com.inventar.inventar.auth.repository.RefreshTokenRepository;
import com.inventar.inventar.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration FIRST_LOCK = Duration.ofMinutes(15);
    private static final Duration SECOND_LOCK = Duration.ofHours(1);
    private static final String BAD_CREDENTIALS = "Login və ya parol səhvdir";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-expiration-days:30}")
    private long refreshDays;

    // noRollbackFor vacibdir: exception atılanda səhv sayğacı və blok bazaya yazılmış qalmalıdır
    @Transactional(noRollbackFor = AuthException.class)
    public TokenResponse login(LoginRequest req) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim();
        String password = req.getPassword() == null ? "" : req.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Login və parolu daxil edin", null, null);
        }

        AppUser user = userRepository.findForUpdate(username)
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS, null, null));

        Instant now = Instant.now();
        releaseExpiredLock(user, now);

        if (isLocked(user, now)) {
            throw locked(user, now);
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw onFailure(user, now);
        }

        user.setFailedAttempts(0);
        user.setLockLevel(0);
        user.setLockUntil(null);
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional(noRollbackFor = AuthException.class)
    public TokenResponse refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Sessiya bitib", null, null);
        }

        RefreshToken stored = refreshTokenRepository.findByTokenHash(sha256(rawRefreshToken))
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Sessiya bitib", null, null));

        Instant now = Instant.now();
        AppUser user = stored.getUser();

        if (stored.getExpiresAt().isBefore(now)) {
            refreshTokenRepository.delete(stored);
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Sessiya bitib", null, null);
        }

        if (isLocked(user, now)) {
            refreshTokenRepository.deleteByUser(user);
            throw locked(user, now);
        }

        // Rotation: köhnə refresh token silinir, yenisi verilir
        refreshTokenRepository.delete(stored);
        return issueTokens(user);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenRepository.deleteByTokenHash(sha256(rawRefreshToken));
        }
    }

    // ---------- Blok məntiqi ----------

    private boolean isLocked(AppUser user, Instant now) {
        return user.getLockUntil() != null && now.isBefore(user.getLockUntil());
    }

    // Blok vaxtı bitibsə sayğac sıfırlanır. 1 saatlıq blok bitibsə səviyyə də sıfırlanır
    private void releaseExpiredLock(AppUser user, Instant now) {
        if (user.getLockUntil() != null && !now.isBefore(user.getLockUntil())) {
            user.setLockUntil(null);
            user.setFailedAttempts(0);
            if (user.getLockLevel() >= 2) {
                user.setLockLevel(0);
            }
        }
    }

    private AuthException onFailure(AppUser user, Instant now) {
        int attempts = user.getFailedAttempts() + 1;

        if (attempts >= MAX_ATTEMPTS) {
            boolean secondLock = user.getLockLevel() >= 1;
            user.setLockUntil(now.plus(secondLock ? SECOND_LOCK : FIRST_LOCK));
            user.setLockLevel(secondLock ? 2 : 1);
            user.setFailedAttempts(0);
            userRepository.save(user);
            refreshTokenRepository.deleteByUser(user); // bütün cihazlarda sessiya bağlanır
            return locked(user, now);
        }

        user.setFailedAttempts(attempts);
        userRepository.save(user);
        return new AuthException(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS, MAX_ATTEMPTS - attempts, null);
    }

    private AuthException locked(AppUser user, Instant now) {
        long millis = Duration.between(now, user.getLockUntil()).toMillis();
        long seconds = Math.max(1, (millis + 999) / 1000);
        return new AuthException(HttpStatus.LOCKED, "Hesab müvəqqəti bloklanıb", null, seconds);
    }

    // ---------- Token-lər ----------

    private TokenResponse issueTokens(AppUser user) {
        String access = jwtService.generateAccessToken(user.getUsername());

        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        String rawRefresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(sha256(rawRefresh));
        rt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        refreshTokenRepository.save(rt);

        return new TokenResponse(access, rawRefresh, jwtService.getAccessSeconds());
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}