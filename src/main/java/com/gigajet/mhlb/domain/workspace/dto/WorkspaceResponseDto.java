package com.gigajet.mhlb.domain.workspace.dto;

import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import lombok.Getter;

public class WorkspaceResponseDto {
    @Getter
    public static class Response {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        public Response(Workspace workspace) {
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

    @Getter
    public static class OrderList {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;
        private Long orders;

        public OrderList(Workspace workspace, Long orders) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
            this.orders = orders;
        }
    }

    @Getter
    public static class People {
        private Long userId;
        private String userImage;
        private String userName;
        private String userJob;
        private String userEmail;
        private String description;
        private String status;
        private Integer color;

        public People(SqlStatus status) {
            this.userId = status.getUser().getId();
            this.userImage = status.getUser().getImage();
            this.userName = status.getUser().getUsername();
            this.userJob = status.getUser().getJob();
            this.userEmail = status.getUser().getEmail();
            this.description = status.getUser().getDescription();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
        }
    }
}
