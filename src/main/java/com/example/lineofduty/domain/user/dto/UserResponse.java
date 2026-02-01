package com.example.lineofduty.domain.user.dto;

import com.example.lineofduty.domain.user.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final String role;
    private final boolean isDeleted;
    private final LocalDateTime deletedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.isDeleted = user.isDeleted();
        this.deletedAt = user.getDeletedAt();
        this.createdAt = user.getCreatedAt();
        this.modifiedAt = user.getModifiedAt();
    }
}