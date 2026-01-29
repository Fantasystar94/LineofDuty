package com.example.lineofduty.domain.user.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserWithdrawResponse {
    private final Long userId;
    private final String email;
    private final boolean isDeleted;
    private final LocalDateTime deletedAt;

    public UserWithdrawResponse(Long userId, String email, boolean isDeleted, LocalDateTime deletedAt) {
        this.userId = userId;
        this.email = email;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }
}