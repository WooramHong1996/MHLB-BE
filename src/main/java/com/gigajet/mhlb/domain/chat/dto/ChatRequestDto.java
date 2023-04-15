package com.gigajet.mhlb.domain.chat.dto;

import lombok.Getter;

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

    @Getter
    public static class ConvertChat {
        private String uuid;
        private Long workspaceId;
        private String message;
        private String email;

        public ConvertChat(ChatRequestDto.Chat chat, String email) {
            this.uuid = chat.getUuid();
            this.workspaceId = chat.getWorkspaceId();
            this.message = chat.getMessage();
            this.email = email;
        }
    }
}