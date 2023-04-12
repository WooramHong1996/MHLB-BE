package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class StatusResponseDto {
    private final Long userId;
    private final String status;
    private final Integer color;

    public StatusResponseDto(Status status) {
        this.userId = status.getUser().getId();
        this.status = status.getStatus().getStatus();
        this.color = status.getStatus().getColor();
    }

    public StatusResponseDto(StatusResponseDto.Convert convert) {
        this.userId = convert.getUserId();
        this.status = convert.getStatus();
        this.color = convert.getColor();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Convert {
        private final Long userId;
        private final String status;
        private final Integer color;
        private final List<WorkspaceUser> workspaces;

        public Convert(Status status, List<WorkspaceUser> workspaces) {
            this.userId = status.getUser().getId();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
            this.workspaces = workspaces;
        }
    }
}
