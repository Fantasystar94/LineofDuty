package com.example.lineofduty.domain.deferment.repository;

import com.example.lineofduty.domain.deferment.Deferment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DefermentRepository extends JpaRepository<Deferment, Long> {
    Page<Deferment> findAll(Pageable pageable);

    Optional<Deferment> findByIdAndUserId(Long defermentId, Long userId);

    Optional<Deferment> findByApplicationId(Long applicationId);
}
