package com.example.lineofduty.domain.user.dto;

import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentApplication;
import com.example.lineofduty.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserAdminResponse {
    private final Long id;
    private final String username;
    private final String email;

    private final String role;

    @JsonProperty("enlistment_application")
    private EnlistmentInfo enlistmentApplication;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;

    public UserAdminResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
        this.enlistmentApplication = null;
    }

    public void setEnlistmentInfo(EnlistmentApplication application) {
        if (application != null) {
            this.enlistmentApplication = new EnlistmentInfo(application);
        }
    }

    @Getter
    static class EnlistmentInfo {
        private final Long id;
        @JsonProperty("enlistment_schedule_id")
        private final Long enlistmentScheduleId;
        @JsonProperty("user_id")
        private final Long userId;
        private final String status;
        @JsonProperty("enlistment_date")
        private final LocalDate enlistmentDate;
        @JsonProperty("created_at")
        private final LocalDateTime createdAt;
        @JsonProperty("modified_at")
        private final LocalDateTime modifiedAt;

        public EnlistmentInfo(EnlistmentApplication application) {
            this.id = application.getId();
            this.enlistmentScheduleId = application.getScheduleId();
            this.userId = application.getUserId();
            this.status = application.getApplicationStatus().name();
            this.enlistmentDate = application.getEnlistmentDate();
            this.createdAt = application.getCreatedAt();
            this.modifiedAt = application.getModifiedAt();
        }
    }
}