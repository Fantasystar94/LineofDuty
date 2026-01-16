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


    // 201 CREATED
    USER_CREATE_SUCCESS("회원가입이 완료되었습니다."),
    PRODUCT_CREATE_SUCCESS("상품 등록 완료"),
    ORDER_CREATE_SUCCESS("주문이 생성되었습니다."),

    // 204 NO Content
    ORDER_DELETE_SUCCESS("주문이 취소되었습니다."),
    //200 enlistment success
    ENLISTMENT_SUCCESS("입영 가능 일정 조회 성공"),
    ENLISTMENT_APPLY_SUCCESS("입영 신청 완료"),
    ENLISTMENT_LIST_SUCCESS("입영 신청 조회 성공"),
    ENLISTMENT_CANCEL_SUCCESS("입영 신청이 취소 완료."),
    ENLISTMENT_APPROVE_SUCCESS("입영 신청 승인 완료."),
    DEFERMENTS_SUCCESS("연기 신청이 접수되었습니다."),
    PRODUCT_DELETE_SUCCESS("상품 삭제 완료");



    private final String message;


}



