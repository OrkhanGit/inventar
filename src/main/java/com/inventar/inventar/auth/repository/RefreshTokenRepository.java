package com.inventar.inventar.auth.repository;

import com.inventar.inventar.auth.model.AppUser;
import com.inventar.inventar.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUser(AppUser user);

    void deleteByTokenHash(String tokenHash);
}