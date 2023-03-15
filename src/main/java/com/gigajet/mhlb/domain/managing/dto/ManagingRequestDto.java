package com.gigajet.mhlb.domain.managing.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
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
}
