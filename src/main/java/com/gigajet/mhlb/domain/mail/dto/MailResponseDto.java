package com.gigajet.mhlb.domain.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MailResponseDto {

    @Schema(description = "초대 코드 체크 DTO")
    @Getter
    @RequiredArgsConstructor
    public static class CheckInviteCode {

        @Schema(description = "초대 코드 링크로 들어온 사용자가 회원인지 확인 Y or N")
        private final String isUser;

    }

}
