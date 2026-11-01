package com.inventar.inventar.sparepart.repository;

import com.inventar.inventar.sparepart.model.SparePart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SparePart s where s.id = :id")
    Optional<SparePart> findByIdForUpdate(@Param("id") Long id);

}
