package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceOrderRepository extends JpaRepository<WorkspaceOrder, Long> {
}
