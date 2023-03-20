package com.gigajet.mhlb.domain.workspaceuser.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkspaceUserService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    // 임시적인 완전 잠깐 쓸 진짜진짜 가짜인 테스트용용
    @Transactional
    public ResponseEntity<SendMessageDto> testJoinUser(Long workspaceId, User user) {

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_PASSWORD));

        workspaceUserRepository.save(new WorkspaceUser(user, workspace, WorkspaceUserRole.MEMBER));

        return SendMessageDto.toResponseEntity(SuccessCode.CHECKUP_EMAIL);
    }

}
