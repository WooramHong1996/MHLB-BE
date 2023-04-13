package com.gigajet.mhlb.domain.alarm.dto;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import lombok.Getter;

public class AlarmResponseDto {
    @Getter
    public static class AlarmChatResponse {
        private Long workspaceId;
        private AlarmTypeEnum type;
        private Boolean unreadMessage;

        public AlarmChatResponse(AlarmRequestDto alarm) {
            this.workspaceId = alarm.getWorkspaceId();
            this.type = alarm.getType();
            this.unreadMessage = alarm.getUnreadMessage();
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
}
