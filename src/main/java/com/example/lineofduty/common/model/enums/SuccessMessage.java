package com.example.lineofduty.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // 200 OK
    AUTH_LOGIN_SUCCESS("로그인 성공"),
    PRODUCT_GET_ONE_SUCCESS("상품 단건 조회 성공"),
    PRODUCT_GET_ALL_SUCCESS("상품 목록 조회 성공"),
    PRODUCT_UPDATE_SUCCESS("상품 수정 완료"),
    PRODUCT_DELETE_SUCCESS("상품 삭제 완료"),

    // 201 CREATED
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    PRODUCT_CREATE_SUCCESS("상품 등록 완료");

    private final String message;
}



