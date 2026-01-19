package com.example.lineofduty.domain.user;

import com.example.lineofduty.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserAdminResponse {
    private final Long id;
    private final String username;
    private final String email;

    @JsonProperty("resident_number")
    private final String residentNumber;

    private final String role;

    @JsonProperty("enlistment_application")
    private Object enlistmentApplication;

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
        this.residentNumber = maskResidentNumber(user.getResidentNumber());
        this.enlistmentApplication = null;
    }

    private String maskResidentNumber(String origin) {
        if (origin == null || origin.length() < 8) return origin;
        return origin.substring(0, 8) + "******";
    }
}