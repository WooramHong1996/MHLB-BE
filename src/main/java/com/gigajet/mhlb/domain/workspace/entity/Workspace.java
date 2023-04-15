package com.gigajet.mhlb.domain.workspace.entity;

import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String image;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<WorkspaceUser> workspaceUsers;

    @Column(nullable = false)
    private Boolean isShow;

    public Workspace(WorkspaceRequestDto.Create workspaceDto, String image) {
        this.title = workspaceDto.getWorkspaceTitle();
        this.description = workspaceDto.getWorkspaceDesc();
        this.image = image;
        this.isShow = true;
    }

    public void changeImage(String image) {
        this.image = image;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDesc(String workspaceDesc) {
        this.description = workspaceDesc;
    }

    public void updateIsShow() {
        this.isShow = false;
    }
}
