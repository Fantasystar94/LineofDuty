package com.example.lineofduty.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserWithdrawResponse {
    private Long userId;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}