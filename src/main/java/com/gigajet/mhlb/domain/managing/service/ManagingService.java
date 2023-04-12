package com.gigajet.mhlb.domain.managing.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.managing.dto.ManagingRequestDto;
import com.gigajet.mhlb.domain.managing.dto.ManagingResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceInviteRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceOrderRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagingService {
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceOrderRepository workspaceOrderRepository;

    private final S3Handler s3Handler;

    @Value("${workspace.default.image}")
    private String defaultImage;

    @Transactional(readOnly = true)
    public ManagingResponseDto.Management findWorkspaceInfo(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        WorkspaceUser manager = checkRole(user, id);
        return new ManagingResponseDto.Management(workspace, manager.getRole());
    }

    @Transactional
    public ManagingResponseDto.Image changeImage(User user, Long id, MultipartFile workspaceImage) throws IOException {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, id);

        String newImage = s3Handler.upload(workspaceImage);

        if (!workspace.getImage().equals(defaultImage)) {
            s3Handler.delete(workspace.getImage());
        }

        workspace.changeImage(newImage);

        return new ManagingResponseDto.Image(newImage);
    }

    @Transactional
    public ManagingResponseDto.Title changeTitle(User user, Long id, String workspaceTitle) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, id);

        workspace.changeTitle(workspaceTitle);

        return new ManagingResponseDto.Title(workspaceTitle);
    }

    @Transactional
    public ManagingResponseDto.Description changeDesc(User user, Long id, String workspaceDesc) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, id);

        workspace.changeDesc(workspaceDesc);

        return new ManagingResponseDto.Description(workspaceDesc);
    }

    @Transactional
    public List<ManagingResponseDto.People> findPeople(User user, Long id) {
        validateWorkspace(id);
        checkRole(user, id);

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        List<ManagingResponseDto.People> responses = new ArrayList<>();

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            responses.add(new ManagingResponseDto.People(workspaceUser.getUser(), workspaceUser.getRole()));
        }

        return responses;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> exilePeople(User user, Long id, Long userid) {
        validateWorkspace(id);
        checkRole(user, id);

        // 조인이 먼저 되는지 조건을 먼저 거는지 확인해야 함
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(userid, id, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUser.offIsShow();

        WorkspaceOrder workspaceOrder = workspaceOrderRepository.findByWorkspaceUserAndIsShow(workspaceUser, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceOrder.offIsShow();

        return SendMessageDto.toResponseEntity(SuccessCode.EXILE_SUCCESS);
    }

    @Transactional
    public ManagingResponseDto.Role changeRole(User user, Long id, Long userid, ManagingRequestDto.Role role) {
        validateWorkspace(id);

        if (role.getUserRole() == WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        checkRole(user, id);

        User changeRoleUser = userRepository.findById(userid).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        WorkspaceUser changeUserWorkspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(changeRoleUser, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        if (role.getUserRole() == changeUserWorkspaceUser.getRole()) {
            throw new CustomException(ErrorCode.SAME_PERMISSION);
        }

        changeUserWorkspaceUser.updateRole(role.getUserRole());

        return new ManagingResponseDto.Role(role.getUserRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> offWorkspaceIsShow(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        WorkspaceUser manager = checkRole(user, id);

        if (manager.getRole() != WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        workspace.updateIsShow();

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);
        for (WorkspaceUser workspaceUser : workspaceUsers) {
            workspaceUser.offIsShow();

            workspaceOrderRepository.findByWorkspaceUserAndIsShow(workspaceUser, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER)).offIsShow();
        }

        workspaceInviteRepository.deleteByWorkspace(manager.getWorkspace());

        return ResponseEntity.ok(SendMessageDto.builder().message("ok").build());
    }

    @Transactional
    public WorkspaceInvite invite(User user, Long id, String invitedUserEmail) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, id);

        Optional<WorkspaceInvite> checkInvite = workspaceInviteRepository.findByWorkspaceAndEmail(workspace, invitedUserEmail);
        // 기존에 초대 한 사람인지 확인
        if (checkInvite.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_INVITED);
        } else {
            // 해당 유저가 회원가입 되어있는지 먼저 확인
            Optional<User> OptionalInviteUser = userRepository.findByEmail(invitedUserEmail);

            if (OptionalInviteUser.isEmpty()) {
                return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, workspace));
            } else {
                User inviteUser = OptionalInviteUser.get();
                Optional<WorkspaceUser> existUser = workspaceUserRepository.findByUserAndWorkspace(inviteUser, workspace);

                if (existUser.isEmpty() || existUser.get().getIsShow() == 0) { //최초 초대거나 재초대인 경우
                    return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, inviteUser, workspace));
                } else if (existUser.get().getIsShow() == 1) {//이미 있는 유저인 경우
                    throw new CustomException(ErrorCode.ALREADY_INVITED);
                }

                return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, inviteUser, workspace));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.Invite> getInvite(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, id);

        List<WorkspaceResponseDto.Invite> inviteList = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByWorkspaceOrderByIdDesc(workspace);

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteList.add(new WorkspaceResponseDto.Invite(workspaceInvite));
        }

        return inviteList;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> cancelInvite(User user, Long id, Long inviteId) {
        validateWorkspace(id);
        checkRole(user, id);

        workspaceInviteRepository.findByWorkspace_IdAndId(id, inviteId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceInviteRepository.deleteById(inviteId);

        return SendMessageDto.toResponseEntity(SuccessCode.CANCEL_INVITE);
    }

    private Workspace validateWorkspace(Long id) {
        return workspaceRepository.findByIdAndIsShow(id, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }

    private WorkspaceUser checkRole(User user, Long id) {
        // 이 부분도 수정해야함. 쿼리 확인.
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DINED));

        if (workspaceUser.getIsShow() == 1 && workspaceUser.getRole() == WorkspaceUserRole.MEMBER) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        return workspaceUser;
    }
}