package com.gigajet.mhlb.domain.workspace.dto;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import lombok.Getter;

public class WorkspaceDto {

    @Getter
    public static class Create{

        private String workspaceTitle;

        private String workspaceDesc;
    }

    @Getter
    public static class AllList{

        private Long workspaceId;

        private String workspaceImage;

        private String workspaceTitle;

        private String workspaceDesc;

        public AllList(Workspace workspace){

            this.workspaceId = workspace.getId();

            this.workspaceImage = workspace.getImage();

            this.workspaceTitle = workspace.getTitle();

            this.workspaceDesc = workspace.getDescription();
        }
    }
}
