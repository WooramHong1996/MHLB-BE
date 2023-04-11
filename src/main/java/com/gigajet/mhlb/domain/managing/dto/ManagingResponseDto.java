package com.gigajet.mhlb.domain.managing.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ManagingResponseDto {
    @Schema(description = "관리자 페이지 워크스페이스 DTO")
    @Getter
    public static class Management {

        @Schema(description = "워크스페이스 id")
        private Long workspaceId;
        @Schema(description = "워크스페이스 이미지")
        private String workspaceImage;
        @Schema(description = "워크스페이스 타이틀")
        private String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보")
        private String workspaceDesc;
        @Schema(description = "로그인한 회원의 권한")
        private WorkspaceUserRole userRole;

        public Management(Workspace workspace, WorkspaceUserRole role) {
            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();

            this.userRole = role;
        }
    }

    @Schema(description = "관리자 페이지 워크스페이스 회원정보 DTO")
    @Getter
    public static class People {
        @Schema(description = "회원 id")
        private Long userId;
        @Schema(description = "회원 이름")
        private String userName;
        @Schema(description = "회원 이메일")
        private String userEmail;
        @Schema(description = "회원 프로필 사진")
        private String userImage;
        @Schema(description = "회원 직업")
        private String userJob;
        @Schema(description = "회원 권한")
        private WorkspaceUserRole userRole;

        public People(User user, WorkspaceUserRole role) {
            this.userId = user.getId();

            this.userName = user.getUsername();

            this.userEmail = user.getEmail();

            this.userImage = user.getImage();

            this.userJob = user.getJob();

            this.userRole = role;
        }
    }

    @Schema(description = "관리자 페이지 워크스페이스 이미지 DTO")
    @Getter
    @RequiredArgsConstructor
    public static class Image {
        @Schema(description = "워크스페이스 이미지")
        private final String workspaceImage;

    }

    @Schema(description = "관리자 페이지 워크스페이스 타이틀 DTO")
    @Getter
    @RequiredArgsConstructor
    public static class Title {
        @Schema(description = "워크스페이스 타이틀")
        private final String workspaceTitle;

    }

    @Schema(description = "관리자 페이지 워크스페이스 상세정보 DTO")
    @Getter
    @RequiredArgsConstructor
    public static class Description {
        @Schema(description = "워크스페이스 상세정보")
        private final String workspaceDesc;

    }

    @Schema(description = "관리자 페이지 워크스페이스 회원 권한 정보 변경 DTO")
    @Getter
    @RequiredArgsConstructor
    public static class Role {
        @Schema(description = "회원 권한")
        private final WorkspaceUserRole userRole;

    }

}
