package com.gigajet.mhlb.domain.workspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceDto;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
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

//    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties("Workspace")
//    private List<WorkspaceUser> workspaceUsers;

    public Workspace(WorkspaceDto.Create workspaceDto, String image) {
        this.title = workspaceDto.getWorkspaceTitle();
        this.description = workspaceDto.getWorkspaceDesc();
        this.image = image;
    }
}
