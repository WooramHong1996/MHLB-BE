package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {
    Optional<WorkspaceUser> findByUserAndWorkspace(User user, Workspace workspace);

    List<WorkspaceUser> findByUserAndIsShow(User user, Boolean isShow);

    List<WorkspaceUser> findByUserAndIsShowOrderByRoleDesc(User user, Boolean isShow);

    Optional<WorkspaceUser> findByUserAndWorkspaceId(User user, Long id);

    List<WorkspaceUser> findByWorkspace_IdAndIsShow(Long id, Boolean isShow);

    Optional<WorkspaceUser> findByUser_IdAndWorkspace_IdAndIsShow(Long userId, Long workspaceId, Boolean isShow);

    Long countByUserAndIsShow(User user, Boolean isShow);
}
