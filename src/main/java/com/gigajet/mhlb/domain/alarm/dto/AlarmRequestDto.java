package com.gigajet.mhlb.domain.alarm.dto;

import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Entity.AlarmTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class AlarmRequestDto {
    @NotNull
    private AlarmTypeEnum type;
    @NotNull
    private Long workspaceId;
    private Long userId;

    public AlarmRequestDto(Alarm alarm) {
        this.type = alarm.getType();
        this.workspaceId = alarm.getWorkspaceId();
        this.userId = alarm.getUser().getId();
    }
}
