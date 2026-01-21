package com.example.lineofduty.domain.enlistmentSchedule;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "enlistment_applications",
    indexes = {
            @Index(
                    name = "idx_app_status",
                    columnList = "application_status, schedule_id"
            )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnlistmentApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    private Long userId;

    private Long scheduleId;

    private LocalDate enlistmentDate;

    public EnlistmentApplication(ApplicationStatus applicationStatus, Long userId, Long scheduleId,LocalDate enlistmentDate) {
        this.applicationStatus = applicationStatus;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.enlistmentDate = enlistmentDate;
    }

    public void changeStatus(ApplicationStatus status) {
        this.applicationStatus = status;
    }

}
