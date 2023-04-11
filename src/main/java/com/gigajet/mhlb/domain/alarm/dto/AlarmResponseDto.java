package com.gigajet.mhlb.domain.alarm.dto;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import lombok.Getter;

public class AlarmResponseDto {
    @Getter
    public static class AlarmChatResponse {
        private final Long workspaceId;
        private final AlarmTypeEnum type;
        private final Boolean unreadMessage;

        public AlarmChatResponse(AlarmRequestDto alarm) {
            this.workspaceId = alarm.getWorkspaceId();
            this.type = alarm.getType();
            this.unreadMessage = false;
        }
    }

    @Getter
    public static class AlarmInviteResponse {
        private final Long workspaceId;
        private final AlarmTypeEnum type;

        public AlarmInviteResponse(Alarm alarm) {
            this.workspaceId = alarm.getWorkspaceId();
            this.type = alarm.getType();
        }
    }

    @Getter
    public static class AlarmResponseVar {
        private final Long workspaceId;
        private final Boolean unreadMessage;

        public AlarmResponseVar(Long workspaceId, Boolean unreadMessage) {
            this.workspaceId = getWorkspaceId();
            this.unreadMessage = getUnreadMessage();
        }
    }
}
