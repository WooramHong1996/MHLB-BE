package com.gigajet.mhlb.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRequestDto {

    @Getter
    public static class UserId {
        private Long userId;
    }

    @Getter
    public static class Chat {
        private String uuid;
        private Long workspaceId;
        private String message;
    }
}