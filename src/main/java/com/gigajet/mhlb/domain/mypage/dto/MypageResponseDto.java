package com.gigajet.mhlb.domain.mypage.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import lombok.Builder;
import lombok.Getter;

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
        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        @Builder
        public AllList(Workspace workspace) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Getter
    public static class Name {
        private String userName;

        public Name(User user) {
            this.userName = user.getUsername();
        }
    }

    @Getter
    public static class Description {
        private String userDesc;
        public Description(User user) {
            this.userDesc = user.getDescription();
        }
    }

    @Getter
    public static class Job {
        private String userJob;

        public Job(User user) {
            this.userJob = user.getJob();
        }
    }

    @Getter
    public static class Image {
        private String userImage;

        public Image(User user) {
            this.userImage = user.getImage();
        }
    }
}
