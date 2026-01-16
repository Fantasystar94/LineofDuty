package com.example.lineofduty.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    /* --- 400 Bad Request --- */
    // 공통/입력값 오류
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 필드값이 누락되었습니다."),

    // 유저 관련
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상이어야 합니다."),
    MISSING_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 누락되었습니다."),

    // 게시판(QnA/공지) 관련
    MISSING_QUESTION_CONTENT(HttpStatus.BAD_REQUEST, "질문 내용은 필수입니다."),
    MISSING_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "댓글 내용은 필수입니다."),

    // 주문/상품 관련
    MISSING_ORDER_ID(HttpStatus.BAD_REQUEST, "orderId가 누락되었습니다."),
    MISSING_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "상품명이 입력되지 않았습니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST, "가격은 0원 이상이어야 합니다."),
    INVALID_STOCK(HttpStatus.BAD_REQUEST, "재고는 0개 이상이어야 합니다."),


    /* --- 401 Unauthorized --- */
    // 인증 실패 (로그인 필요)
    INVALID_AUTH_INFO(HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    ADMIN_PERMISSION_REQUIRED(HttpStatus.UNAUTHORIZED, "관리자 권한이 필요합니다."),

    /* --- 403 Forbidden --- */
    // 인가 실패 (권한 부족 - 작성자가 아님 등)
    NO_MODIFY_PERMISSION(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),
    NO_DELETE_PERMISSION(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다."),


    /* --- 404 Not Found --- */
    // 리소스 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "입영 신청 내역이 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "입영 일정이 존재하지 않습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지를 찾을 수 없습니다."),



    /* --- 409 Conflict --- */
    // 데이터 충돌
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    ALREADY_PAID_ORDER(HttpStatus.CONFLICT, "이미 결제된 주문입니다."),
    DUPLICATE_SCHEDULE(HttpStatus.CONFLICT, "이미 신청된 유저입니다"),
    INVALID_APPLICATION_STATUS(HttpStatus.CONFLICT,"승인 할 수 없습니다");

    private final HttpStatus status;
    private final String message;
}
