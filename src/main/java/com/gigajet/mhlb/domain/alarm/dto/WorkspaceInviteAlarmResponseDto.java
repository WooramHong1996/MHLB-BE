package com.gigajet.mhlb.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorkspaceInviteAlarmResponseDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConvertWorkspaceInviteAlarm {

        private boolean invitedWorkspace;
        private Long userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class workspaceInviteAlarm {

        private boolean invitedWorkspace;
    }

}
