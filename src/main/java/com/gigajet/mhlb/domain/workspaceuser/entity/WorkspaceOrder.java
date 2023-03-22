package com.gigajet.mhlb.domain.workspaceuser.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class WorkspaceOrder {
    //순서저장용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "workspaceUser_id")
    private WorkspaceUser workspaceUser;
    @Column
    private Long orders;
    @Column
    private Integer isShow;

    public WorkspaceOrder(Long orders, WorkspaceUser workspaceUser) {
        this.orders = orders;
        this.workspaceUser = workspaceUser;
        isShow = 1;
    }

    public void updateOrder(Long orders) {
        this.orders = orders;
    }

    public void updateIsShow(){this.isShow = 0;}
}
