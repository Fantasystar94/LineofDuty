package com.example.lineofduty.domain.enlistmentSchedule.repository;

import com.example.lineofduty.entity.EnlistmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EnlistmentScheduleRepository extends JpaRepository<EnlistmentSchedule, Long> {

}
