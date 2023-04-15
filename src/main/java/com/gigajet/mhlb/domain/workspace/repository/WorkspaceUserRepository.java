package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, Long> {
    Optional<WorkspaceUser> findByUserAndWorkspaceAndIsShowTrue(User user, Workspace workspace);

    Optional<WorkspaceUser> findByUserAndWorkspaceAndIsShow(User user, Workspace workspace, Boolean isShow);

    Optional<WorkspaceUser> findByUserAndWorkspace(User user, Workspace workspace);

    List<WorkspaceUser> findAllByUser_IdAndIsShowTrue(Long id);

    List<WorkspaceUser> findByUserAndIsShowTrueOrderByRoleDesc(User user);

    List<WorkspaceUser> findByWorkspaceAndIsShow(Workspace workspace, Boolean isShow);

    Long countByUserAndIsShow(User user, Boolean isShow);
}
