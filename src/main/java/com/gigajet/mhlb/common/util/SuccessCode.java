package com.gigajet.mhlb.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    SIGNUP_SUCCESS(HttpStatus.OK, "registration success"),
    LOGIN_SUCCESS(HttpStatus.OK, "login success"),
    CHECKUP_SUCCESS(HttpStatus.OK, "email available"),
    LOGOUT_SUCCESS(HttpStatus.OK, "bye bye");

    private final HttpStatus httpStatus;
    private final String message;
}