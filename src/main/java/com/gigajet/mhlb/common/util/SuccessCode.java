package com.gigajet.mhlb.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    CHECKUP_SUCCESS(HttpStatus.OK, "중복 테스트 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공");

    private final HttpStatus httpStatus;
    private final String message;
}