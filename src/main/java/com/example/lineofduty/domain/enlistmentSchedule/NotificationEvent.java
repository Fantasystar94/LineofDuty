package com.example.lineofduty.domain.enlistmentSchedule;


public record NotificationEvent(NotificationType type, Long userId, Long applicationId, Long defermentId) {

    //신청했을경우
    public static NotificationEvent enlistmentRequested(Long userId, Long applicationId) {
        return new NotificationEvent(NotificationType.ENLISTMENT_REQUESTED, userId, applicationId, null);
    }

    //어드민 컨펌일 경우
    public static NotificationEvent enlistmentConfirmed(Long userId, Long applicationId) {
        return new NotificationEvent(NotificationType.ENLISTMENT_CONFIRMED, userId, applicationId, null);
    }

    //연기 승인일 경우
    public static NotificationEvent defermentApproved(Long userId, Long defermentId) {
        return new NotificationEvent(NotificationType.DEFERMENT_APPROVED, userId, null, defermentId);
    }

    //연기 반려일 경우
    public static NotificationEvent defermentRejected(Long userId, Long defermentId) {
        return new NotificationEvent(NotificationType.DEFERMENT_REJECTED, userId, null, defermentId);
    }

}
