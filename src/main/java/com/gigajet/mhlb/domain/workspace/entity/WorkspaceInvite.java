package com.gigajet.mhlb.domain.workspace.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class WorkspaceInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    public WorkspaceInvite(String email, Workspace workspace) {//비회원
        this.email = email;
        this.workspace = workspace;
    }

    public WorkspaceInvite(String email, User user, Workspace workspace) {//회원
        this.email = email;
        this.user = user;
        this.workspace = workspace;
    }
}
