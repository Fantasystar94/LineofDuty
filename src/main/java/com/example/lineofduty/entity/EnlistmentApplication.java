package com.example.lineofduty.entity;

import com.example.lineofduty.common.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enlistment_applications")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnlistmentApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    private Long userId;

    private Long scheduleId;

    public EnlistmentApplication(ApplicationStatus applicationStatus, Long userId, Long scheduleId) {
        this.applicationStatus = applicationStatus;
        this.userId = userId;
        this.scheduleId = scheduleId;
    }

}
