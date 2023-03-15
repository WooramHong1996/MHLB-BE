package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {

    Optional<WorkspaceUser> findByUserAndWorkspace(User user, Workspace workspace);

    List<WorkspaceUser> findByUser(User user);

    Optional<WorkspaceUser> findByUserAndWorkspaceId(User user, Long id);

    List<WorkspaceUser> findByWorkspace_Id(Long id);

    int countByWorkspace(Workspace workspace);
}
