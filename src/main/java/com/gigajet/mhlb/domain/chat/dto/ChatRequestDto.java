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

    @Getter
    public static class Convert {
        private String uuid;
        private Long workspaceId;
        private String message;
        private String email;

        public Convert(ChatRequestDto.Chat chat, String email) {
            this.uuid = chat.getUuid();
            this.workspaceId = chat.getWorkspaceId();
            this.message = chat.getMessage();
            this.email = email;
        }
    }
}