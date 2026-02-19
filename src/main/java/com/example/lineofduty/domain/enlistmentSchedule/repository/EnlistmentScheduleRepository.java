package com.example.lineofduty.domain.enlistmentSchedule.repository;

import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface EnlistmentScheduleRepository extends JpaRepository<EnlistmentSchedule, Long> {

    //count db 점유율 높음. groupBy 마찬가지.
    @Query("select count(e) != 0 from" +
            " EnlistmentSchedule e" +
            " where e.enlistmentDate >= :today " +
            "and e.remainingSlots > 0" +
            "and e.id =:scheduleId"
    )
    boolean checkUserCanApplyToEnlistmentDate(@Param("today")LocalDate today,@Param("scheduleId")Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from EnlistmentSchedule s where s.id = :id")
    Optional<EnlistmentSchedule> findByIdWithLock(@Param("id") Long id);

    EnlistmentSchedule findByEnlistmentDate(LocalDate enlistmentDate);

    @Query("select s from EnlistmentSchedule s where s.id = :scheduleId")
    EnlistmentSchedule findByIdTest(Long scheduleId);

    boolean existsByEnlistmentDateBetween(LocalDate enlistmentDateAfter, LocalDate enlistmentDateBefore);
}
