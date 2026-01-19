package com.example.lineofduty.domain.deferment;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "deferments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Deferment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deferment_id")
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefermentStatus status;

    private LocalDate requestedUntil;

    public Deferment(Long applicationId, Long userId, String reason, DefermentStatus status, LocalDate requestedUntil) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
        this.requestedUntil = requestedUntil;
    }

    public void approve() {
        this.status = DefermentStatus.APPROVED;
    }

    public void reject() {
        this.status = DefermentStatus.REJECTED;
    }

}
