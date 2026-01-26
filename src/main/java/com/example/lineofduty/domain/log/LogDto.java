package com.example.lineofduty.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogDto {

    private Long id;
    private Long userId;
    private String errorMessage;
    private LocalDateTime createdAt;

    public static LogDto from(SystemLog log) {
        return new LogDto(
                log.getId(),
                log.getUserId(),
                log.getErrorMessage(),
                log.getCreatedAt());
    }
}
