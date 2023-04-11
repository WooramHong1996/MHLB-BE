package com.gigajet.mhlb.domain.managing.dto;

import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole;
import lombok.Getter;

public class ManagingRequestDto {

    @Getter
    public static class Title {
        private String workspaceTitle;
    }

    @Getter
    public static class Desc {
        private String workspaceDesc;
    }

    @Getter
    public static class Role {
        private WorkspaceUserRole userRole;
    }

}
