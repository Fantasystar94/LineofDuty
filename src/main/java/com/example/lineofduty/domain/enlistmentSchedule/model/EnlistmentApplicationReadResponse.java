package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnlistmentApplicationReadResponse {
    private Long applicationId;
    private LocalDate enlistmentDate;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static EnlistmentApplicationReadResponse from(EnlistmentApplication application) {
        return new EnlistmentApplicationReadResponse(
                application.getId(),
                application.getEnlistmentDate(),
                application.getApplicationStatus(),
                application.getCreatedAt(),
                application.getModifiedAt()
        );
    }
}
