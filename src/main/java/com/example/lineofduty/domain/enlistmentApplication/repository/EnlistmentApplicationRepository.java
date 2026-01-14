package com.example.lineofduty.domain.enlistmentApplication.repository;

import com.example.lineofduty.entity.EnlistmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnlistmentApplicationRepository extends JpaRepository<EnlistmentApplication, Long> {
    boolean existsByUserId(Long userId);

    boolean existsByScheduleId(Long scheduleId);
}
