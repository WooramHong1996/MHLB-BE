package com.gigajet.mhlb.domain.workspace.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class WorkspaceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "workspaceUser_id")
    private WorkspaceUser workspaceUser;
    @Column
    private Long orders;
    @Column
    private Boolean isShow;

    public WorkspaceOrder(Long orders, WorkspaceUser workspaceUser) {
        this.orders = orders;
        this.workspaceUser = workspaceUser;
        isShow = true;
    }

    public void updateOrder(Long orders) {
        this.orders = orders;
    }

    public void offIsShow() {
        this.isShow = false;
    }

    public void onIsShow() {
        this.isShow = true;
    }
}
