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

    // auth
    SIGNUP_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),

    // user
    USER_READ_SUCCESS("내 정보 조회 성공"),
    USER_UPDATE_SUCCESS("내 정보 수정 성공"),
    USER_DELETE_SUCCESS("회원탈퇴 성공"),

    // admin
    USER_ALL_READ_SUCCESS("회원 전체 조회 성공"),
    USER_READ_ADMIN_SUCCESS("회원 정보 조회 성공"),
    USER_DELETE_ADMIN_SUCCESS("회원 탈퇴 완료")



    ;
    private final String message;
}