package com.inventar.inventar.auth.repository;

import com.inventar.inventar.auth.model.AppUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    // Eyni anda gələn sorğular səhv sayğacını keçməsin deyə sətir kilidlənir
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM AppUser u WHERE u.username = :username")
    Optional<AppUser> findForUpdate(@Param("username") String username);
    Optional<AppUser> findByUsername(String username);
}