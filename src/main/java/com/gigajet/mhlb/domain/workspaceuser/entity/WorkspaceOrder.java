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
    @OneToOne(fetch = FetchType.LAZY)
    private WorkspaceUser user;
    @Column
    private Integer orders;

    public WorkspaceOrder(Integer orders) {
        this.orders = orders;
    }
}
