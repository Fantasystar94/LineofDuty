package com.example.lineofduty.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // 200 OK
    AUTH_LOGIN_SUCCESS("로그인 성공"),
    QNA_CREATE_SUCCESS("질문이 생성되었습니다."),
    QNA_READ_SUCCESS("질문 단건 조회 성공"),
    ORDER_GET_SUCCESS("주문이 성공적으로 조회되었습니다."),
    ORDER_UPDATE_SUCCESS("주문이 수정되었습니다."),


    // 201 CREATED
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    QNA_UPDATE_SUCCESS("질문이 수정되었습니다"),
    QNA_DELETE_SUCCESS("질문이 삭제되었습니다"),
    NOTICE_CREATE_SUCCESS("공지사항이 등록되었습니다."),
    NOTICE_READ_SUCCESS("공지사항 상세 조회 성공");
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    ORDER_CREATE_SUCCESS("주문이 생성되었습니다."),

    // 204 NO Content
    ORDER_DELETE_SUCCESS("주문이 취소되었습니다.");


    private final String message;
}



