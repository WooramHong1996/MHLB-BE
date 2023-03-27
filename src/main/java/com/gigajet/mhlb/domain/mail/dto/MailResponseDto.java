package com.gigajet.mhlb.domain.mail.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MailResponseDto {

    @Getter
    @RequiredArgsConstructor
    public static class CheckInviteCode {

        private final String isUser;

    }

}
