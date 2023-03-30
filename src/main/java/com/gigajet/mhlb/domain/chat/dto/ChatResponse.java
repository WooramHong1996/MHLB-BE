package com.gigajet.mhlb.domain.chat.dto;

import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponse {
    @Getter
    public static class GetUuid {
        private String uuid;

        public GetUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    @Getter
    public static class Inbox {
        private String uuid;
        private String userImage;
        private String userName;
        private LocalDateTime lastChat;
        private String message;
        private Integer unreadMessages;

        public Inbox(ChatRoom chatRoom, User user, Integer count) {
            this.uuid = chatRoom.getInBoxId();
            this.userImage = user.getImage();
            this.userName = user.getUsername();
            this.lastChat = chatRoom.getLastChat();
            this.message = chatRoom.getLastMessage();
        }
    }

    @Getter
    public static class Chat {
        private Long senderId;
        private String message;

        public Chat(Long senderId, String message) {
            this.message = message;
            this.senderId = senderId;
        }
    }
}
