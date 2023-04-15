package com.gigajet.mhlb.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    ACCESS_DENIED(BAD_REQUEST, "E-11", "access denied"),

    DUPLICATE_EMAIL(BAD_REQUEST, "U-01", "duplicate email"),
    UNREGISTER_USER(BAD_REQUEST, "U-02", "unregister user"),
    WRONG_PASSWORD(BAD_REQUEST, "U-03", "wrong password"),
    SOCIAL_USER(BAD_REQUEST, "U-04", "social user"),

    WRONG_WORKSPACE_ID(BAD_REQUEST, "W-01", "wrong workspace id"),
    WRONG_USER(BAD_REQUEST, "W-02", "wrong user"),
    NOT_SAME_EMAIL(BAD_REQUEST, "W-03", "not same email"),
    SAME_PERMISSION(BAD_REQUEST, "W-04", "same role"),
    NOT_SOCIAL_EMAIL(BAD_REQUEST, "W-05", "not social email"),

    INVALID_CODE(BAD_REQUEST, "M-01", "invalid code"),
    INVALID_INVITATION(BAD_REQUEST, "M-02", "Invalid invitation"),
    ALREADY_INVITED(BAD_REQUEST, "M-03", "already invited"),

    NULL_MULTIPART_FILE(BAD_REQUEST, "E-01", "null multipart file"),
    RESIZING_FAILED(BAD_REQUEST, "E-02", "resizing failed"),
    NOT_IMAGE(BAD_REQUEST, "E-03", "not image"),

    STATUS_NOT_CHANGED(BAD_REQUEST, "S-01", "같은 상태로는 변경할 수 없습니다."),
    STATUS_NOT_EXIST(BAD_REQUEST, "S-02", "존재하지 않는 상태값 입니다"),
    UNDEFINED_REQUEST(BAD_REQUEST, "S-03", "올바르지 않은 요청입니다"),

    /* 401 UNAUTHORIZED : 인증 실패 */
    INVALID_TOKEN(UNAUTHORIZED, "SC-01", "invalid token"),
    NULL_TOKEN(UNAUTHORIZED, "SC-02", "null token"),

    /* 403 FORBIDDEN : 인가 실패 */
    PERMISSION_DINED(FORBIDDEN, "SC-11", "forbidden"),

    /* 500 */
    FAIL_CONVERT(INTERNAL_SERVER_ERROR, "E-04", "convert failed");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}