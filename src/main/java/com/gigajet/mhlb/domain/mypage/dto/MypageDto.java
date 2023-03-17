package com.gigajet.mhlb.domain.mypage.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import lombok.Builder;
import lombok.Getter;

public class MypageDto {
    @Getter
    public static class UserResponse {
        private final String userName;

        private final long id;

        private final String userDesc;

        private final String userImage;

        private final String userJob;

        @Builder
        public UserResponse(User user) {
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
    public static class NameResponse{
        private String userName;

        public NameResponse(User user) {
            this.userName = user.getUsername();
        }
    }

    @Getter
    public static class DescResponse{
        private String userDesc;
        public DescResponse(User user) {
            this.userDesc = user.getDescription();
        }
    }

    @Getter
    public static class JobResponse{
        private String userJob;

        public JobResponse(User user) {
            this.userJob = user.getJob();
        }
    }

    @Getter
    public static class ImageResponse{
        private String userImage;

        public ImageResponse(User user) {
            this.userImage = user.getImage();
        }
    }
}
