package com.gigajet.mhlb.domain.chat.entity;

import lombok.Getter;

@Getter
public class UserAndMessage {
    private Long userId;
    private Long unread;

    public UserAndMessage(Long userId) {
        this.userId = userId;
        this.unread = 0l;
    }

    public void resetUnread() {
        this.unread = 0l;
    }

    public void addUnread() {
        this.unread++;
    }
}
