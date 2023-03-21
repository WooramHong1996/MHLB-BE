package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long> {
    List<WorkspaceInvite> findByWorkspace(Workspace workspace);

    Optional<WorkspaceInvite> findByWorkspace_IdAndId(Workspace workspace, Long id);

    Optional<WorkspaceInvite> findByWorkspaceAndEmail(Workspace workspace, String email);
}
