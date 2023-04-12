package com.gigajet.mhlb.domain.workspace.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

public class WorkspaceRequestDto {

    @Getter
    public static class Create {
        @NotNull
        private String workspaceTitle;

        @NotNull
        private String workspaceDesc;
    }

    @Getter
    public static class Invite {
        @NotNull
        private String email;
    }

    @Getter
    public static class Orders {
        @NotNull
        private List<Order> orders;
    }

    @Getter
    public static class Order {
        private Long workspaceId;
        private Long orderNum;
    }
}
