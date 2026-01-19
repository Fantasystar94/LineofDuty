package com.example.lineofduty.domain.enlistmentApplication.repository;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentApplication.EnlistmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnlistmentApplicationRepository extends JpaRepository<EnlistmentApplication, Long> {

    @Query("select count(e) != 0 from EnlistmentApplication e where e.userId =:userId and (e.applicationStatus IN ('PENDING','CONFIRMED'))")
    boolean existsByUserIdAndStatus(@Param("userId") Long userId);

    boolean existsByScheduleId(Long scheduleId);

    @Query("select count(e) != 0 from EnlistmentApplication e where e.userId =:userId and e.scheduleId =:scheduleId")
    boolean existsByUserIdAndScheduleId(@Param("userId") Long UserId, @Param("scheduleId") Long ScheduleId);

    List<EnlistmentApplication> findEnlistmentApplicationByApplicationStatus(ApplicationStatus applicationStatus);

    Optional<EnlistmentApplication> findByScheduleId(Long scheduleId);
}
