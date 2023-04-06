package com.gigajet.mhlb.domain.status.dto;

import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private Long userId;
    private String status;
    private Integer color;

    public StatusResponseDto(SqlStatus status) {
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Convert{
        private Long userId;
        private String status;
        private Integer color;
        private List<WorkspaceUser> workspaces;

        public Convert(SqlStatus status, List<WorkspaceUser> workspaces){
            this.userId = status.getUser().getId();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
            this.workspaces = workspaces;
        }
    }
}
