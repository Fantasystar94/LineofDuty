package com.example.lineofduty.domain.enlistmentSchedule;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "enlistment_applications",
        indexes = {
                @Index(
                        name = "idx_app_status",
                        columnList = "application_status, schedule_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnlistmentApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private LocalDate enlistmentDate;

    @OneToOne(mappedBy = "application")
    @JoinColumn(name = "deferment_id", nullable = false, unique = true)
    private Deferment deferment;

    private void setDeferment(Deferment deferment) {
        this.deferment = deferment;
    }

    public EnlistmentApplication(Long userId, Long scheduleId, LocalDate enlistmentDate) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.enlistmentDate = enlistmentDate;
        this.applicationStatus = ApplicationStatus.REQUESTED; // 시작점 고정
    }

    //관리자 승인
    public void confirm() {
        if (applicationStatus != ApplicationStatus.REQUESTED) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }
        this.applicationStatus = ApplicationStatus.CONFIRMED;
    }

    //관리자 반려
    public void reject() {
        if (applicationStatus != ApplicationStatus.REQUESTED) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }
        this.applicationStatus = ApplicationStatus.REJECTED;
    }

    public void cancel() {
        if (applicationStatus == ApplicationStatus.CANCELED) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }
        this.applicationStatus = ApplicationStatus.CANCELED;
    }

    //입영일 변경
    public void applyDeferredDate(LocalDate changedDate) {
        if (applicationStatus != ApplicationStatus.REQUESTED) {
            throw new CustomException(ErrorMessage.INVALID_APPLICATION_STATUS);
        }
        this.enlistmentDate = changedDate;
    }


    public boolean isRequested() {
        return applicationStatus != ApplicationStatus.REQUESTED;
    }

    public boolean isConfirmed() {
        return applicationStatus == ApplicationStatus.CONFIRMED;
    }
}

