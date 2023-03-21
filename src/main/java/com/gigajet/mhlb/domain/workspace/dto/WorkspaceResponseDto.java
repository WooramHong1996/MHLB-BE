package com.gigajet.mhlb.domain.workspace.dto;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import lombok.Getter;

public class WorkspaceResponseDto {
    @Getter
    public static class CreateResponse {
        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        public CreateResponse(Workspace workspace) {
            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Getter
    public static class AllList {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        public AllList(Workspace workspace) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Getter
    public static class InfoAndRoll {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        private WorkspaceUserRole userRole;

        public InfoAndRoll(Workspace workspace, WorkspaceUserRole role) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();

            this.userRole = role;
        }
    }

    @Getter
    public static class Invite {
        private Long inviteId;

        private String email;

        public Invite(WorkspaceInvite invite) {
            this.inviteId = invite.getId();

            this.email = invite.getEmail();
        }
    }
}
