package com.gigajet.mhlb.domain.managing.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagingService {
    private final WorkspaceUserRepository workspaceUserRepository;

    private final WorkspaceRepository workspaceRepository;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ManagingResponseDto.Management management(User user, Long id) {
        return new ManagingResponseDto.Management(checkRole(user, id).getWorkspace());
    }

    @Transactional
    public String imagePatch(User user, Long id, MultipartFile image) {
        //임시코드
        String newimage = image.getName();
        //임시코드

        checkRole(user, id);

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        workspace.imageChange(newimage);

        return workspace.getImage();
    }

    @Transactional
    public String titlePatch(User user, Long id, String workspaceTitle) {
        Workspace workspace = checkRole(user, id).getWorkspace();

        workspace.titleChange(workspaceTitle);

        return workspace.getTitle();
    }

    @Transactional
    public String descPatch(User user, Long id, String workspaceDesc) {
        Workspace workspace = checkRole(user, id).getWorkspace();

        workspace.descChange(workspaceDesc);

        return workspace.getDescription();
    }

    @Transactional
    public List getPeople(User user, Long id) {
        checkRole(user, id);

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByWorkspace_Id(id);

        List<ManagingResponseDto.People> responses = new ArrayList<>();

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            responses.add(new ManagingResponseDto.People(workspaceUser.getUser(), workspaceUser.getRole()));
        }

        return responses;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deletePeople(User user, Long id, Long userid) {
        checkRole(user, id);

        workspaceUserRepository.deleteByUser_IdAndAndWorkspace_Id(userid, id);

        return ResponseEntity.ok(SendMessageDto.builder().message("ok").build());
    }

    @Transactional
    public WorkspaceUserRole changeRole(User user, Long id, Long userid, WorkspaceUserRole userRole) {
        if (userRole == WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        checkRole(user, id);

        User changeUser = userRepository.findById(userid).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        WorkspaceUser changeUserWorkspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(changeUser, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        if (userRole == changeUserWorkspaceUser.getRole()) {
            throw new CustomException(ErrorCode.SAME_PERMISSION);
        }

        changeUserWorkspaceUser.updateRole(userRole);

        return userRole;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteWorkspace(User user, Long id) {
        WorkspaceUser manager = checkRole(user, id);

        if (manager.getRole() != WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        workspaceRepository.deleteById(id);
        return ResponseEntity.ok(SendMessageDto.builder().message("ok").build());
    }

    private WorkspaceUser checkRole(User user, Long id) {
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DINED));

        if (workspaceUser.getRole() == WorkspaceUserRole.MEMBER) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        return workspaceUser;
    }
}