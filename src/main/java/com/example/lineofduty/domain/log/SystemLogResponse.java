package com.example.lineofduty.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SystemLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String action;
    private String errorMessage;
    private String requestData;
    private LocalDateTime createdAt;

    public static SystemLogResponse of(SystemLog log, String username, String email) {
        return new SystemLogResponse(
                log.getId(),
                log.getUserId(),
                username,
                email,
                log.getAction(),
                log.getErrorMessage(),
                log.getRequestData(),
                log.getCreatedAt()
        );
    }
}
