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
    ORDER_GET_SUCCESS("주문이 성공적으로 조회되었습니다."),
    ORDER_UPDATE_SUCCESS("주문이 수정되었습니다."),
    QNA_CREATE_SUCCESS("질문이 생성되었습니다."),
    QNA_READ_SUCCESS("질문 단건 조회 성공"),
    QNA_UPDATE_SUCCESS("질문이 수정되었습니다"),
    NOTICE_READ_SUCCESS("공지사항 상세 조회 성공"),
    NOTICE_UPDATE_SUCCESS("공지가 수정되었습니다."),

    // 201 CREATED

    NOTICE_CREATE_SUCCESS("공지사항이 등록되었습니다."),
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    PRODUCT_CREATE_SUCCESS("상품 등록 완료"),
    ORDER_CREATE_SUCCESS("주문이 생성되었습니다."),

    // 204 NO Content
    PRODUCT_DELETE_SUCCESS("상품 삭제 완료"),
    ORDER_DELETE_SUCCESS("주문이 취소되었습니다."),
    QNA_DELETE_SUCCESS("질문이 삭제되었습니다"),
    NOTICE_DELETE_SUCCESS("공지가 삭제되었습니다.");


    private final String message;
}



