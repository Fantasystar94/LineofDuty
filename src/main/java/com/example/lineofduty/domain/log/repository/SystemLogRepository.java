package com.example.lineofduty.domain.log.repository;

import com.example.lineofduty.domain.log.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
