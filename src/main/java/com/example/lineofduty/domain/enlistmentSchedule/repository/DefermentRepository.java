package com.example.lineofduty.domain.enlistmentSchedule.repository;

import com.example.lineofduty.domain.enlistmentSchedule.Deferment;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentsReadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DefermentRepository extends JpaRepository<Deferment, Long> {

    @Query("SELECT new com.example.lineofduty.domain.enlistmentSchedule.model.DefermentsReadResponse( " +
            "d.id, d.reason, d.status, d.changedDate, d.createdAt, d.modifiedAt, u.username) " +
            "FROM Deferment d " +
            "JOIN User u ON d.userId = u.id")
    Page<DefermentsReadResponse> findDefermentList(Pageable pageable);

    Optional<Deferment> findByIdAndUserId(Long defermentId, Long userId);

}
