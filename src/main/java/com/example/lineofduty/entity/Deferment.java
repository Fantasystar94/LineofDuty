package com.example.lineofduty.entity;

import com.example.lineofduty.common.model.enums.DefermentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Deferment(Long applicationId, Long userId, String reason, DefermentStatus status) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }
}
