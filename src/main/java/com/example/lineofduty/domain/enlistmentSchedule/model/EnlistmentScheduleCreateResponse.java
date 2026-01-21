package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EnlistmentScheduleCreateResponse {
    private Long applicationId;
    private Long scheduleId;
    private LocalDate enlistmentDate;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static EnlistmentScheduleCreateResponse from(EnlistmentApplication enlistmentApplication) {
        return new EnlistmentScheduleCreateResponse(
                enlistmentApplication.getId(),
                enlistmentApplication.getScheduleId(),
                enlistmentApplication.getEnlistmentDate(),
                enlistmentApplication.getApplicationStatus(),
                enlistmentApplication.getCreatedAt(),
                enlistmentApplication.getModifiedAt()
        );
    }
}
