package com.gigajet.mhlb.domain.mypage.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MypageResponseDto {
    @Getter
    public static class Info {
        private final String userName;

        private final long id;

        private final String userDesc;

        private final String userImage;

        private final String userJob;

        @Builder
        public Info(User user) {
            this.userName = user.getUsername();

            this.id = user.getId();

            this.userDesc = user.getDescription();

            this.userImage = user.getImage();

            this.userJob = user.getJob();
        }
    }

    @Getter
    public static class AllList {
        private List<WorkspaceList> workspaceList;
        private List<InviteList> inviteList;

        @Builder
        public AllList(List<InviteList> inviteLists, List<WorkspaceList> workspaceList) {
            this.inviteList = inviteLists;
            this.workspaceList = workspaceList;
        }
    }

    @Getter
    public static class WorkspaceList {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        private WorkspaceUserRole userRole;

        @Builder
        public WorkspaceList(Workspace workspace, WorkspaceUser workspaceUser) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();

            this.userRole = workspaceUser.getRole();
        }
    }

    @Getter
    public static class InviteList {
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        @Builder
        public InviteList(Workspace workspace) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Getter
    public static class Name {
        private String userName;

        public Name(String userName) {
            this.userName = userName;
        }
    }

    @Getter
    public static class Description {
        private String userDesc;
        public Description(String userDesc) {
            this.userDesc = userDesc;
        }
    }

    @Getter
    public static class Job {
        private String userJob;

        public Job(String userJob) {
            this.userJob = userJob;
        }
    }

    @Getter
    public static class Image {
        private String userImage;

        public Image(String userImage) {
            this.userImage = userImage;
        }
    }
}
