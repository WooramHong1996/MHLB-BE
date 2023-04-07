package com.gigajet.mhlb.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;

@Getter
@Setter
public class UnreadMessageCount {
    private Long otherUserId;
    private Long unreadCount;
    private String roomId;
    private String type;

    public UnreadMessageCount(ChatRequestDto.Chat roomMessage) {
        this.type = "UNREAD";
        this.otherUserId = roomMessage.getOtherUserIds();
        this.roomId = roomMessage.getUuid();
        this.unreadCount = roomMessage.getCount();
    }
}
