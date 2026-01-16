package com.example.lineofduty.domain.enlistmentSchedule.model.response;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.entity.BaseEntity;
import com.example.lineofduty.entity.EnlistmentApplication;
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

    public static EnlistmentScheduleCreateResponse from(EnlistmentApplication enlistmentApplication) {
        return new EnlistmentScheduleCreateResponse(
                enlistmentApplication.getId(),
                enlistmentApplication.getScheduleId(),
                enlistmentApplication.getEnlistmentDate(),
                enlistmentApplication.getApplicationStatus()
        );
    }
}
