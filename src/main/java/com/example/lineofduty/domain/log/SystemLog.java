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

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
