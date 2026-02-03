package com.example.lineofduty.domain.enlistmentSchedule.repository;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnlistmentApplicationRepository extends JpaRepository<EnlistmentApplication, Long> {

    List<EnlistmentApplication> findEnlistmentApplicationByApplicationStatus(ApplicationStatus applicationStatus);

    Optional<EnlistmentApplication> findByUserId(Long userId);

    boolean existsByUserIdAndApplicationStatusIn(
            Long userId,
            List<ApplicationStatus> statuses
    );

    @Query("""
        select a
        from EnlistmentApplication a
        join fetch Deferment d on d.application.id = a.id
        join fetch EnlistmentSchedule s on s.id = a.scheduleId
        where a.applicationStatus = :status
    """)
    List<EnlistmentApplication> findRequestedWithDefermentAndSchedule(
            @Param("status") ApplicationStatus status
    );
}
