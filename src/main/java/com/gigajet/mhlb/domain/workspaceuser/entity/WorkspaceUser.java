package com.gigajet.mhlb.domain.workspaceuser.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
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
    private Integer isShow;

    @OneToOne(mappedBy = "workspaceUser")
    private WorkspaceOrder workspaceorder;

    public WorkspaceUser(User user, Workspace workspace, WorkspaceUserRole role) {
        this.user = user;
        this.workspace = workspace;
        this.role = role;
        this.isShow = 1;
    }

    public WorkspaceUser(User user, Workspace workspace) {
        this.user = user;
        this.workspace = workspace;
        this.role = WorkspaceUserRole.MEMBER;
        this.isShow = 1;
    }

    public void updateRole(WorkspaceUserRole role) {
        this.role = role;
    }

    public void offIsShow() {
        this.isShow = 0;
    }

    public void onIsShow() {
        this.isShow = 1;
    }
}
