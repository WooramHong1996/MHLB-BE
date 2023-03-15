package com.gigajet.mhlb.domain.mypage.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MypageDto {
    @Getter
    public static class UserResponse {
        private final String userName;
        private final String userEmail;
        private final String userDesc;
        private final String userImage;
        private final String userJob;

        @Builder
        public UserResponse(User user) {
            this.userName = user.getUsername();
            this.userEmail = user.getEmail();
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
}
