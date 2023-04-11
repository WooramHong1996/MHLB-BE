package com.gigajet.mhlb.domain.alarm.dto;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import lombok.Getter;

public class AlarmResponse {
    @Getter
    public static class AlarmChatResponse {
        private final Long workspaceId;
        private final AlarmTypeEnum type;
        private final Integer isShow;

        public AlarmChatResponse(AlarmRequestDto alarm) {
            this.workspaceId = alarm.getWorkspaceId();
            this.type = alarm.getType();
            this.isShow = 0;
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
        private final Boolean isShow;

        public AlarmResponseVar(Long workspaceId, Boolean isShow) {
            this.workspaceId = getWorkspaceId();
            this.isShow = getIsShow();
        }
    }
//    @Getter
//    public static class AllAlarm{
//        private final List<AlarmChatResponse> alarmChatResponselist;
//        private final List<AlarmInviteResponse> alarmInviteResponseList;
//
//        public AllAlarm(List<AlarmChatResponse> alarmChatResponselist, List<AlarmInviteResponse> alarmInviteResponseList) {
//            this.alarmChatResponselist = alarmChatResponselist;
//            this.alarmInviteResponseList = alarmInviteResponseList;
//        }
//    }
}
