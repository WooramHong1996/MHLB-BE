package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long> {
    List<WorkspaceInvite> findByWorkspaceOrderByIdDesc(Workspace workspace);

    Optional<WorkspaceInvite> findByWorkspace_IdAndId(Long workspaceId, Long inviteId);

    Optional<WorkspaceInvite> findByEmailAndWorkspace(String email, Workspace workspace);

    List<WorkspaceInvite>findAllByEmail(String email);

    int countByEmail(String email);

    void deleteByEmailAndWorkspace(String email, Workspace workspace);

    void deleteByWorkspace(Workspace workspace);
}
