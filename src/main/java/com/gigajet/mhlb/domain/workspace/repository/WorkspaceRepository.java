package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
