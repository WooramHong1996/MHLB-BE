package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WorkspaceOrderRepository extends JpaRepository<WorkspaceOrder, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<WorkspaceOrder> findByWorkspaceUser_UserAndIsShowOrderByOrders(User user, Boolean isShow);

    @Modifying
    @Query("UPDATE WorkspaceOrder SET orders = ?1 WHERE workspaceUser.id = ?2")
    void orderUpdate(Long orders, Long id);

    Optional<WorkspaceOrder> findByWorkspaceUserAndIsShow(WorkspaceUser workspaceUser, Boolean isShow);
}