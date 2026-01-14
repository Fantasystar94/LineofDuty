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
    ENLISTMENT_SUCCESS("입영 가능 일정 조회 성공");

    private final String message;


}



