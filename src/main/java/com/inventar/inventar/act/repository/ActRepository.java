package com.inventar.inventar.act.repository;

import com.inventar.inventar.act.model.Act;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
}
