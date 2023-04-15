package com.gigajet.mhlb.domain.workspace.dto;

import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class WorkspaceResponseDto {
    @Schema(description = "메인 페이지 워크스페이스 정보 DTO")
    @Getter
    public static class Response {
        @Schema(description = "워크스페이스 id", example = "1")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지", example = "url")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀", example = "Workspace title")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보", example = "Workspace description")
        private final String workspaceDesc;

        public Response(Workspace workspace) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Schema(description = "메인 페이지 워크스페이스 정보 DTO")
    @Getter
    public static class AllList {
        @Schema(description = "워크스페이스 id", example = "1")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지", example = "url")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀", example = "Workspace title")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보", example = "Workspace description")
        private final String workspaceDesc;
        @Schema(description = "안 읽은 메시지", example = "true")
        private final Boolean unreadMessage;

        public AllList(Workspace workspace, Boolean unreadMessage) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
            this.unreadMessage = unreadMessage;
        }
    }

    @Schema(description = "메인 페이지 워크스페이스 정보, 회원 권한 DTO")
    @Getter
    public static class InfoAndRoll {
        @Schema(description = "워크스페이스 id", example = "1")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지", example = "url")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀", example = "Workspace title")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보", example = "Workspace description")
        private final String workspaceDesc;
        @Schema(description = "회원 권한", example = "ADMIN")
        private final WorkspaceUserRole userRole;

        public InfoAndRoll(Workspace workspace, WorkspaceUserRole role) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
            this.userRole = role;
        }
    }

    @Schema(description = "워크스페이스 초대 DTO")
    @Getter
    public static class Invite {
        @Schema(description = "WorkspaceUser id", example = "1")
        private final Long inviteId;
        @Schema(description = "초대 회원 이메일", example = "test@test.com")
        private final String email;

        public Invite(WorkspaceInvite invite) {
            this.inviteId = invite.getId();
            this.email = invite.getEmail();
        }
    }

    @Schema(description = "정렬된 워크스페이스 DTO")
    @Getter
    public static class OrderList {
        @Schema(description = "워크스페이스 id", example = "1")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지", example = "url")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀", example = "Workspace title")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보", example = "Workspace description")
        private final String workspaceDesc;
        @Schema(description = "워크스페이스 순서", example = "1")
        private final Long orders;

        public OrderList(Workspace workspace, Long orders) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
            this.orders = orders;
        }
    }

    @Schema(description = "워크스페이스에 속한 회원정보 DTO")
    @Getter
    public static class People {
        @Schema(description = "회원 id", example = "1")
        private final Long userId;
        @Schema(description = "회원 프로필 이미지", example = "url")
        private final String userImage;
        @Schema(description = "회원 이름", example = "User name")
        private final String userName;
        @Schema(description = "회원 직업", example = "User job")
        private final String userJob;
        @Schema(description = "회원 이메일", example = "test@test.com")
        private final String userEmail;
        @Schema(description = "회원 상태 메세지", example = "User status message")
        private final String userDesc;
        @Schema(description = "회원 상태", example = "User status")
        private final String status;
        @Schema(description = "회원 상태 색깔", example = "1")
        private final Integer color;

        public People(Status status) {
            this.userId = status.getUser().getId();
            this.userImage = status.getUser().getImage();
            this.userName = status.getUser().getUsername();
            this.userJob = status.getUser().getJob();
            this.userEmail = status.getUser().getEmail();
            this.userDesc = status.getUser().getDescription();
            this.status = status.getStatus().getStatus();
            this.color = status.getStatus().getColor();
        }
    }
}
