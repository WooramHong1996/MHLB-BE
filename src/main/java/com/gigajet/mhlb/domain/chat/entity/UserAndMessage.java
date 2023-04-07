package com.gigajet.mhlb.domain.chat.entity;

import lombok.Getter;

@Getter
public class UserAndMessage {
    private Long userId;
    private Long unread;
    private String uuid;

    public UserAndMessage(Long userId, String uuid) {
        this.userId = userId;
        this.unread = 0l;
        this.getUuid();
    }

    public void resetUnread() {
        this.unread = 0l;
    }

    public void addUnread() {
        this.unread++;
    }
}
