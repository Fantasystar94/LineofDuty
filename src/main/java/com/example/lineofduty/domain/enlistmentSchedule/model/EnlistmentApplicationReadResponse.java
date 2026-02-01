package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import com.example.lineofduty.domain.user.User;
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
    private String userName;
    public static EnlistmentApplicationReadResponse from(EnlistmentApplication application, User user) {
        return new EnlistmentApplicationReadResponse(
                application.getId(),
                application.getEnlistmentDate(),
                application.getApplicationStatus(),
                application.getCreatedAt(),
                application.getModifiedAt(),
                user.getUsername()
        );
    }
}
