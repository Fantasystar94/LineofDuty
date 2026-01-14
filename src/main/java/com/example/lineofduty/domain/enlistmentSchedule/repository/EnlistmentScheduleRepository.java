package com.example.lineofduty.domain.enlistmentSchedule.repository;

import com.example.lineofduty.entity.EnlistmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnlistmentScheduleRepository extends JpaRepository<EnlistmentSchedule, Long> {
}
