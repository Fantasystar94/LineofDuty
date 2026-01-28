package com.example.lineofduty.domain.enlistmentSchedule.repository;
import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnlistmentApplicationRepository extends JpaRepository<EnlistmentApplication, Long> {

    List<EnlistmentApplication> findEnlistmentApplicationByApplicationStatus(ApplicationStatus applicationStatus);

    Optional<EnlistmentApplication> findByUserId(Long userId);

    boolean existsByUserIdAndApplicationStatusIn(
            Long userId,
            List<ApplicationStatus> statuses
    );
}
