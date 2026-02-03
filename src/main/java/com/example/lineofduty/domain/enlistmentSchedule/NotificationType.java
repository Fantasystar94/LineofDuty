package com.example.lineofduty.domain.enlistmentSchedule;


public enum NotificationType {
    ENLISTMENT_REQUESTED,     // 유저 신청
    ENLISTMENT_CONFIRMED,     // 어드민 승인(확정)
    DEFERMENT_APPROVED,       // 연기 승인
    DEFERMENT_REJECTED        // 연기 반려
}
