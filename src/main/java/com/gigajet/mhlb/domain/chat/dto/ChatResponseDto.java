package com.gigajet.mhlb.domain.chat.dto;

import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponseDto {
    @Getter
    public static class GetUuid {
        private String uuid;

        public GetUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Inbox {
        private String uuid;
        private String userImage;
        private String userName;
        private LocalDateTime lastChat;
        private String message;
        private Long userId;
        private Long unreadMessages;

        public void inbox(ChatRoom chatRoom, User user) {
            this.uuid = chatRoom.getInBoxId();
            this.userId = user.getId();
            this.userImage = user.getImage();
            this.userName = user.getUsername();
            this.lastChat = chatRoom.getLastChat();
            this.message = chatRoom.getLastMessage();
        }

        public void unreadMessage(Long unreadMessages) {
            this.unreadMessages = unreadMessages;
        }
    }

    @Getter
    public static class ChatData {
        private Long messageId;
        private Long userId;
        private String message;
        private LocalDateTime createdAt;

        public ChatData(Convert convert) {
            this.messageId = convert.getMessageId();
            this.message = convert.getMessage();
            this.userId = convert.getUserId();
            this.createdAt = LocalDateTime.parse(convert.getCreatedAt());
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Convert {
        private Long messageId;
        private Long userId;
        private String message;
        private String inboxId;
        private String createdAt;

        public Convert(Chat chat) {
            this.messageId = chat.getMessageId();
            this.message = chat.getMessage();
            this.userId = chat.getSenderId();
            this.createdAt = chat.getCreatedAt().toString();
            this.inboxId = chat.getInBoxId();
        }

    }

    @Getter
    public static class Chatting {
        private Long messageId;
        private Long userId;
        private String message;
        private LocalDateTime createdAt;

        public Chatting(Chat chat) {
            this.messageId = chat.getMessageId();
            this.message = chat.getMessage();
            this.userId = chat.getSenderId();
            this.createdAt = chat.getCreatedAt();
        }
    }
}