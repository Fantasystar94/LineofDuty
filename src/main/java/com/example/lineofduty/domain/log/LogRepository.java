package com.example.lineofduty.domain.log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<SystemLog, Long> {

    @Query("SELECT l FROM SystemLog l WHERE l.userId IN (SELECT u.id FROM User u WHERE u.username LIKE %:username%)")
    Page<SystemLog> searchSystemLogsByUsername(@Param("username") String username, Pageable pageable);
}
