package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceOrderRepository extends JpaRepository<WorkspaceOrder, Long> {
    List<WorkspaceOrder> findByWorkspaceUser_UserAndIsShowOrderByOrders(User user, Integer isShow);
}