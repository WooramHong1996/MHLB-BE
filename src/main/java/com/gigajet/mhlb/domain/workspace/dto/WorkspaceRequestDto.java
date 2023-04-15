package com.gigajet.mhlb.domain.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

public class WorkspaceRequestDto {

    @Getter
    public static class Create {
        @Schema(description = "워크스페이스 타이틀" , example = "Workspace title")
        @NotNull
        private String workspaceTitle;
        @Schema(description = "워크스페이스 상세정보" , example = "Workspace description")
        @NotNull
        private String workspaceDesc;
    }

    @Getter
    public static class Invite {
        @Schema(description = "초대를 받을 사용자의 이메일" , example = "test@test.com")
        @NotNull
        private String email;
    }

    @Getter
    public static class Orders {
        @Schema(description = "워크스페이스 목록 순서 변경한 리스트" , example = "{\"workspaceId\" : 1, \"orderNum\" : 1}")
        @NotNull
        private List<Order> orders;
    }

    @Getter
    public static class Order {
        @Schema(description = "워크스페이스 id" , example = "1")
        private Long workspaceId;
        @Schema(description = "워크스페이스 변경한 순번" , example = "1")
        private Long orderNum;
    }
}
