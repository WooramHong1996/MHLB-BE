package com.gigajet.mhlb.common.dto;

import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class SendMessageDto {
    private String message;
    private int statusCode;

    @Builder
    public SendMessageDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static SendMessageDto of(SuccessCode successCode) {
        return SendMessageDto.builder()
                .statusCode(successCode.getHttpStatus().value())
                .message(successCode.getMessage())
                .build();
    }

    public static SendMessageDto of(ErrorCode errorCode) {
        return SendMessageDto.builder()
                .statusCode(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }

    public static ResponseEntity<SendMessageDto> toResponseEntity(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getHttpStatus().value())
                .body(SendMessageDto.builder()
                        .statusCode(successCode.getHttpStatus().value())
                        .message(successCode.getMessage())
                        .build());
    }

    public static ResponseEntity<SendMessageDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(SendMessageDto.builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .build());
    }
}