package com.gigajet.mhlb.domain.workspace.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class WorkspaceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false)
    private WorkspaceUserRole role;

    @Column(nullable = false)
    private Boolean isShow;

    @OneToOne(mappedBy = "workspaceUser")
    private WorkspaceOrder workspaceOrder;

    public WorkspaceUser(User user, Workspace workspace, WorkspaceUserRole role) {
        this.user = user;
        this.workspace = workspace;
        this.role = role;
        this.isShow = true;
    }

    public WorkspaceUser(User user, Workspace workspace) {
        this.user = user;
        this.workspace = workspace;
        this.role = WorkspaceUserRole.MEMBER;
        this.isShow = true;
    }

    public void updateRole(WorkspaceUserRole role) {
        this.role = role;
    }

    public void offIsShow() {
        this.isShow = false;
    }

    public void onIsShow() {
        this.isShow = true;
    }
}
