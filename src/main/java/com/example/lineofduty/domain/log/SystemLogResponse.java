package com.example.lineofduty.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SystemLogResponse {
    private Long logId;
    private Long userId;
    private String username;
    private String action;
    private String errorMessage;
    private String requestData;
    private LocalDateTime createdAt;

    public static SystemLogResponse of(SystemLog log, String username) {
        return new SystemLogResponse(
                log.getId(),
                log.getUserId(),
                username,
                log.getAction(),
                log.getErrorMessage(),
                log.getRequestData(),
                log.getCreatedAt()
        );
    }
}
