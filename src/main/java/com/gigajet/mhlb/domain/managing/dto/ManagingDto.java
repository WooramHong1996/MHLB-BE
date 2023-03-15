package com.gigajet.mhlb.domain.managing.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import lombok.Getter;

public class ManagingDto {
    @Getter
    public static class ManagementResponse {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        public ManagementResponse(Workspace workspace) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }


    }

    @Getter
    public static class Title{
        private String workspaceTitle;
    }

    @Getter
    public static class Desc{
        private String workspaceDesc;
    }

    @Getter
    public static class PeopleResponse {
        private Long id;

        private String userName;

        private String userEmail;

        private String userImage;

        private String userJob;

        private WorkspaceUserRole userRole;

        public PeopleResponse(User user, WorkspaceUserRole role) {
            this.id = user.getId();

            this.userName = user.getUsername();

            this.userEmail = user.getEmail();

            this.userImage = user.getImage();

            this.userJob = user.getJob();

            this.userRole = role;
        }
    }
}
