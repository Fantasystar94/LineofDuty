package com.example.lineofduty.domain.enlistmentSchedule;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private EnlistmentApplication application;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefermentStatus status;

    private LocalDate changedDate;

    public Deferment(EnlistmentApplication application, Long userId, String reason, DefermentStatus status, LocalDate changedDate) {
        this.application = application;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
        this.changedDate = changedDate;
    }
}
