package com.gigajet.mhlb.global.common.dto;

import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Schema(description = "응답 메세지 DTO")
@Getter
public class SendMessageDto {

    @Schema(description = "메세지")
    private final String message;
    @Schema(description = "상태코드")
    private final String code;

    @Builder
    public SendMessageDto(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public static SendMessageDto of(SuccessCode successCode) {
        return SendMessageDto.builder()
                .message(successCode.getMessage())
                .build();
    }

    public static SendMessageDto of(ErrorCode errorCode) {
        return SendMessageDto.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ResponseEntity<SendMessageDto> toResponseEntity(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getHttpStatus().value())
                .body(SendMessageDto.builder()
                        .message(successCode.getMessage())
                        .build());
    }

    public static ResponseEntity<SendMessageDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(SendMessageDto.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}