package com.gigajet.mhlb.domain.workspace.dto;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import com.sun.istack.NotNull;
import lombok.Getter;

public class WorkspaceRequestDto {

    @Getter
    public static class Create {
        @NotNull
        private String workspaceTitle;

        @NotNull
        private String workspaceDesc;
    }
}
