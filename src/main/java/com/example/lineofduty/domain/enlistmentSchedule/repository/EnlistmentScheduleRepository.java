package com.example.lineofduty.domain.enlistmentSchedule.repository;

import com.example.lineofduty.entity.EnlistmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EnlistmentScheduleRepository extends JpaRepository<EnlistmentSchedule, Long> {

    //count db 점유율 높음. groupBy 마찬가지.
    @Query("select count(e) != 0 from" +
            " EnlistmentSchedule e" +
            " where e.enlistmentDate >= :today " +
            "and e.remainingSlots > 0" +
            "and e.id =:scheduleId"
    )
    boolean checkUserCanApplyToEnlistmentDate(@Param("today")LocalDate today,@Param("scheduleId")Long scheduleId);
}
