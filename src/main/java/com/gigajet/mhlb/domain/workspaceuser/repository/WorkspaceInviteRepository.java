package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long> {
    List<WorkspaceInvite> findByWorkspaceOrderByIdDesc(Workspace workspace);

    Optional<WorkspaceInvite> findByWorkspace_IdAndId(Long workspaceId, Long inviteId);

    Optional<WorkspaceInvite> findByWorkspace_IdAndUserId(Long workspaceId, Long userId);

    Optional<WorkspaceInvite> findByWorkspaceAndEmail(Workspace workspace, String email);
    List<WorkspaceInvite>findByUser(User user);
    void deleteByUser_IdAndWorkspace_Id(Long userId, Long workspaceId);
    void deleteByWorkspace(Workspace workspace);
}
