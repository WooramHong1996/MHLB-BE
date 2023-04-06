package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WorkspaceOrderRepository extends JpaRepository<WorkspaceOrder, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<WorkspaceOrder> findByWorkspaceUser_UserAndIsShowOrderByOrders(User user, Integer isShow);

    @Modifying
    @Query("UPDATE WorkspaceOrder w SET w.orders = :orders WHERE w.workspaceUser.id = :id")
    void orderUpdate(Long orders, Long id);

    Optional<WorkspaceOrder> findByWorkspaceUserAndIsShow(WorkspaceUser workspaceUser, Integer isShow);
}