package com.gigajet.mhlb.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ChatRequestDto {

    @Getter
    public static class UserId {
        private Long userId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Chat {
        private String uuid;
        private Long messageId;
        private Long workspaceId;
        private String message;
        private MessageType type; // 메시지 타입
        private Long senderId;
        private int count;
        private Long otherUserIds;
        private LocalDateTime creatAt;

        public Chat(ChatRequestDto.Chat chatMessageDto, int count) {
            this.type = MessageType.UNREAD_MESSAGE_COUNT_ALARM; // 메시지 타입
            this.uuid = chatMessageDto.uuid; // 방 이름
            this.otherUserIds = chatMessageDto.otherUserIds; // 상대방 privateKey
            this.count = count; //안읽은 메세지 개수
        }
    }

    public enum MessageType {
        TALK,
        UNREAD_MESSAGE_COUNT_ALARM
    }
}