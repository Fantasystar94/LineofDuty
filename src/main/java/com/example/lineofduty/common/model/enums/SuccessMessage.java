package com.example.lineofduty.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // 200 OK
    AUTH_LOGIN_SUCCESS("로그인 성공"),


    // 201 CREATED
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    //200 enlistment success
    ENLISTMENT_SUCCESS("입영 가능 일정 조회 성공"),
    ENLISTMENT_APPLY_SUCCESS("입영 신청 완료"),
    ENLISTMENT_LIST_SUCCESS("입영 신청 조회 성공"),
    ENLISTMENT_CANCEL_SUCCESS("입영 신청이 취소 완료."),
    ENLISTMENT_APPROVE_SUCCESS("입영 신청 승인 완료."),
    DEFERMENTS_SUCCESS("연기 신청이 접수되었습니다.");
    private final String message;


}



