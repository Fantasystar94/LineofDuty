package com.example.lineofduty.common.exception;

public final class ValidationMessage {

    private ValidationMessage() {}

    // --- 공통 메시지 ---
    public static final String NOT_BLANK_DEFAULT = "값은 필수입니다.";

    // --- User 관련 ---
    public static final String USERNAME_NOT_BLANK = "닉네임은 필수입니다.";
    public static final String USERNAME_SIZE = "닉네임은 10글자를 넘길 수 없습니다.";
    public static final String EMAIL_FORMAT = "올바른 이메일 형식이 아닙니다.";
    public static final String PASSWORD_NOT_BLANK = "비밀번호는 필수입니다.";
    public static final String PASSWORD_PATTERN_MESSAGE = "비밀번호는 영어와 숫자, 특수문자를 최소 1개 이상 포함해서 8자리 이상 입력해주세요.";
    public static final String NAME_NOT_BLANK = "이름은 필수입니다.";
    public static final String LOGIN_REQUIRED = "username과 password는 필수입니다";
    public static final String PASSWORD_REQUIRED = "비밀번호를 입력해주세요.";

    // --- Task 관련 ---
    public static final String TASK_TITLE_ASSIGNEE_NOT_BLANK = "제목과 담당자는 필수입니다.";

    // --- Team 관련 ---
    public static final String TEAM_NAME_NOT_BLANK = "팀 이름은 필수입니다.";
    public static final String USER_ID_NOT_NULL = "userId는 필수입니다.";

    // --- Comment 관련 ---
    public static final String COMMENT_CONTENT_NOT_BLANK = "댓글 내용은 필수입니다.";

    // --- Product 관련 ---
    public static final String MISSING_PRODUCT_NAME = "상품명이 입력되지 않았습니다.";
    public static final String INVALID_PRICE = "가격은 0원 이상이어야 합니다.";
    public static final String INVALID_STOCK = "재고는 0개 이상이어야 합니다.";

    // --- 정규식 ---
    public static final String PASSWORD_REGEXP = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%])(?=\\S+$).{8,}$";
}
