package com.gigajet.mhlb.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    DUPLICATE_EMAIL(BAD_REQUEST, "duplicate email"),
    UNREGISTER_USER(BAD_REQUEST, "unregister user"),
    WRONG_PASSWORD(BAD_REQUEST, "wrong password"),
    WRONG_ADMIN_TOKEN(BAD_REQUEST, "wrong admin token"),
    DUPLICATE_TITLE(BAD_REQUEST, "duplicate title"),
    WRONG_WORKSPACE_ID(BAD_REQUEST, "wrong workspace id"),
    WRONG_USER(BAD_REQUEST, "wrong user"),
    NOT_SAME_EMAIL(BAD_REQUEST, "not same email"),
    SAME_PERMISSION(BAD_REQUEST, "same role"),
    NOT_SOCIAL_EMAIL(BAD_REQUEST, "not social email"),
    ALREADY_INVITED(BAD_REQUEST, "already invited"),
    INVALID_CODE(BAD_REQUEST, "invalid code"),
    STATUS_NOT_CHANGED(BAD_REQUEST, "같은 상태로는 변경할 수 없습니다."),
    STATUS_NOT_EXIST(BAD_REQUEST, "존재하지 않는 상태값 입니다"),
    INVALID_INVITATION(BAD_REQUEST, "Invalid invitation"),


    /* 401 UNAUTHORIZED : 인증 실패 */
    INVALID_TOKEN(UNAUTHORIZED, "invalid token"),
    NULL_TOKEN(UNAUTHORIZED, "null token"),

    /* 403 FORBIDDEN : 인가 실패 */
    PERMISSION_DINED(FORBIDDEN, "forbidden"),

    /* 500 */
    FAIL_CONVERT(INTERNAL_SERVER_ERROR, "convert failed");


    private final HttpStatus httpStatus;
    private final String message;

}