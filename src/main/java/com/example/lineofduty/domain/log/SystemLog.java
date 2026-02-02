package com.example.lineofduty.domain.log;

import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Getter
@NoArgsConstructor
public class SystemLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action")
    private String action;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData;


    public SystemLog(Long userId, String action, String errorMessage, String requestData) {
        this.userId = userId;
        this.action = action;
        this.errorMessage = errorMessage;
        this.requestData = requestData;
    }
}
