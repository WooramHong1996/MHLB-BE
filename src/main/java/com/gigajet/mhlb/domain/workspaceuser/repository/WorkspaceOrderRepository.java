package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceOrderRepository extends JpaRepository<WorkspaceOrder, Long> {
    List<WorkspaceOrder> findByWorkspaceUser_UserAndIsShowOrderByOrders(User user, Integer isShow);

    Optional<WorkspaceOrder> findByWorkspaceUserAndIsShow(WorkspaceUser workspaceUser, Integer isShow);
}