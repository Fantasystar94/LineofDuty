package com.example.lineofduty.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<SystemLog, Long> {
}
