package com.gigajet.mhlb.domain.mypage.dto;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MypageResponseDto {

    @Schema(description = "마이페이지 회원정보 DTO")
    @Getter
    public static class Info {
        @Schema(description = "회원 이름")
        private final String userName;
        @Schema(description = "회원 id")
        private final long id;
        @Schema(description = "회원 상태 메세지")
        private final String userDesc;
        @Schema(description = "회원 프로필 이미지")
        private final String userImage;
        @Schema(description = "회원 직업")
        private final String userJob;

        public Info(User user) {
            this.userName = user.getUsername();
            this.id = user.getId();
            this.userDesc = user.getDescription();
            this.userImage = user.getImage();
            this.userJob = user.getJob();
        }
    }

    @Schema(description = "마이페이지 워크스페이스 리스트 DTO")
    @Getter
    public static class AllList {
        @Schema(description = "가입되어있는 워크스페이스 리스트")
        private final List<WorkspaceList> workspaceList;
        @Schema(description = "초대받은 워크스페이스 리스트")
        private final List<InviteList> inviteList;

        @Builder
        public AllList(List<InviteList> inviteLists, List<WorkspaceList> workspaceList) {
            this.inviteList = inviteLists;
            this.workspaceList = workspaceList;
        }
    }

    @Schema(description = "가입되어있는 워크스페이스 리스트 DTO")
    @Getter
    public static class WorkspaceList {
        @Schema(description = "워크스페이스 id")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보")
        private final String workspaceDesc;
        @Schema(description = "워크스페이스에서의 유저 권한")
        private final WorkspaceUserRole userRole;

        @Builder
        public WorkspaceList(Workspace workspace, WorkspaceUser workspaceUser) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
            this.userRole = workspaceUser.getRole();
        }
    }

    @Schema(description = "초대받은 워크스페이스 리스트 DTO")
    @Getter
    public static class InviteList {
        @Schema(description = "워크스페이스 id")
        private final Long workspaceId;
        @Schema(description = "워크스페이스 이미지")
        private final String workspaceImage;
        @Schema(description = "워크스페이스 타이틀")
        private final String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보")
        private final String workspaceDesc;

        public InviteList(Workspace workspace) {
            this.workspaceId = workspace.getId();
            this.workspaceImage = workspace.getImage();
            this.workspaceTitle = workspace.getTitle();
            this.workspaceDesc = workspace.getDescription();
        }
    }

    @Schema(description = "마이페이지 회원 이름 DTO")
    @Getter
    public static class Name {
        @Schema(description = "회원 이름")
        private final String userName;

        public Name(String userName) {
            this.userName = userName;
        }
    }

    @Schema(description = "마이페이지 회원 상태 메세지 DTO")
    @Getter
    public static class Description {
        @Schema(description = "회원 상태 메세지")
        private final String userDesc;

        public Description(String userDesc) {
            this.userDesc = userDesc;
        }
    }

    @Schema(description = "마이페이지 회원 직업 DTO")
    @Getter
    public static class Job {
        @Schema(description = "회원 직업")
        private final String userJob;

        public Job(String userJob) {
            this.userJob = userJob;
        }
    }

    @Schema(description = "마이페이지 회원 프로필 이미지 DTO")
    @Getter
    public static class Image {
        @Schema(description = "회원 프로필 이미지")
        private final String userImage;

        public Image(String userImage) {
            this.userImage = userImage;
        }
    }

}
