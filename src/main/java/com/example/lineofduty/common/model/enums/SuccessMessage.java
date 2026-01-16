package com.example.lineofduty.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // 200 OK
    ORDER_GET_SUCCESS("주문이 성공적으로 조회되었습니다."),
    ORDER_UPDATE_SUCCESS("주문이 수정되었습니다."),

    // 201 CREATED
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    ORDER_CREATE_SUCCESS("주문이 생성되었습니다."),

    // 204 NO Content
    ORDER_DELETE_SUCCESS("주문이 취소되었습니다."),


    // auth
    AUTH_LOGIN_SUCCESS("로그인 성공"),

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