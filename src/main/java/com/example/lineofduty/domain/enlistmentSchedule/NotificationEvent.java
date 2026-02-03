package com.example.lineofduty.domain.enlistmentSchedule;


import java.time.LocalDate;

public record NotificationEvent(NotificationType type, Long userId, Long applicationId, Long defermentId, LocalDate enlistmentDate, LocalDate changeDate) {

    //신청했을경우
    public static NotificationEvent enlistmentRequested(Long userId, Long applicationId, LocalDate enlistmentDate) {
        return new NotificationEvent(NotificationType.ENLISTMENT_REQUESTED, userId, applicationId, null, enlistmentDate, null);
    }

    //어드민 컨펌일 경우
    public static NotificationEvent enlistmentConfirmed(Long userId, Long applicationId, LocalDate enlistmentDate) {
        return new NotificationEvent(NotificationType.ENLISTMENT_CONFIRMED, userId, applicationId, null, enlistmentDate, null);
    }

    //연기 요청일 경우
    public static NotificationEvent defermentRequested(Long userId, Long defermentId, LocalDate enlistmentDate, LocalDate changeDate) {
        return new NotificationEvent(NotificationType.DEFERMENT_APPROVED, userId, null, defermentId, enlistmentDate, changeDate);
    }

    //연기 승인일 경우
    public static NotificationEvent defermentApproved(Long userId, Long defermentId, LocalDate enlistmentDate,LocalDate changeDate) {
        return new NotificationEvent(NotificationType.DEFERMENT_APPROVED, userId, null, defermentId, enlistmentDate, changeDate);
    }

    //연기 반려일 경우
    public static NotificationEvent defermentRejected(Long userId, Long defermentId, LocalDate changeDate) {
        return new NotificationEvent(NotificationType.DEFERMENT_REJECTED, userId, null, defermentId, null, changeDate);
    }

}
