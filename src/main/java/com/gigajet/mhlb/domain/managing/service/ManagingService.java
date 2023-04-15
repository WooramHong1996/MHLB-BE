package com.gigajet.mhlb.domain.managing.service;

import com.gigajet.mhlb.domain.alarm.dto.WorkspaceInviteAlarmResponseDto;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.S3Handler;
import com.gigajet.mhlb.global.common.util.SuccessCode;
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
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${workspace.default.image}")
    private String defaultImage;

    @Transactional(readOnly = true)
    public ManagingResponseDto.Management findWorkspaceInfo(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        WorkspaceUser manager = checkRole(user, workspace);
        return new ManagingResponseDto.Management(workspace, manager.getRole());
    }

    @Transactional
    public ManagingResponseDto.Image changeImage(User user, Long id, MultipartFile workspaceImage) throws IOException {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

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
        checkRole(user, workspace);

        workspace.changeTitle(workspaceTitle);

        return new ManagingResponseDto.Title(workspaceTitle);
    }

    @Transactional
    public ManagingResponseDto.Description changeDesc(User user, Long id, String workspaceDesc) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        workspace.changeDesc(workspaceDesc);

        return new ManagingResponseDto.Description(workspaceDesc);
    }

    @Transactional
    public List<ManagingResponseDto.People> findPeople(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByWorkspaceAndIsShow(workspace, true);

        List<ManagingResponseDto.People> responses = new ArrayList<>();

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            responses.add(new ManagingResponseDto.People(workspaceUser.getUser(), workspaceUser.getRole()));
        }

        return responses;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> exilePeople(User user, Long id, Long opponentsId) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        User opponents = userRepository.findById(opponentsId).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));

        // 조인이 먼저 되는지 조건을 먼저 거는지 확인해야 함
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(opponents, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUser.offIsShow();

        WorkspaceOrder workspaceOrder = workspaceOrderRepository.findByWorkspaceUserAndIsShow(workspaceUser, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceOrder.offIsShow();

        return SendMessageDto.toResponseEntity(SuccessCode.EXILE_SUCCESS);
    }

    @Transactional
    public ManagingResponseDto.Role changeRole(User user, Long id, Long userid, ManagingRequestDto.Role role) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        if (role.getUserRole() == WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        User changeRoleUser = userRepository.findById(userid).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        WorkspaceUser changeUserWorkspaceUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(changeRoleUser, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        if (role.getUserRole() == changeUserWorkspaceUser.getRole()) {
            throw new CustomException(ErrorCode.SAME_PERMISSION);
        }

        changeUserWorkspaceUser.updateRole(role.getUserRole());

        return new ManagingResponseDto.Role(role.getUserRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> offWorkspaceIsShow(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        WorkspaceUser manager = checkRole(user, workspace);

        if (manager.getRole() != WorkspaceUserRole.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        workspace.updateIsShow();

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByWorkspaceAndIsShow(workspace, true);
        for (WorkspaceUser workspaceUser : workspaceUsers) {
            workspaceUser.offIsShow();

            workspaceOrderRepository.findByWorkspaceUserAndIsShow(workspaceUser, true).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER)).offIsShow();
        }

        workspaceInviteRepository.deleteByWorkspace(manager.getWorkspace());

        return ResponseEntity.ok(SendMessageDto.builder().message("ok").build());
    }

    @Transactional
    public WorkspaceInvite invite(User user, Long id, String invitedUserEmail) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        Optional<WorkspaceInvite> checkInvite = workspaceInviteRepository.findByEmailAndWorkspace(invitedUserEmail, workspace);
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
                Optional<WorkspaceUser> existUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(inviteUser, workspace);

                if (existUser.isEmpty() || !existUser.get().getIsShow()) { //최초 초대거나 재초대인 경우
                    redisTemplate.convertAndSend("workspaceInviteAlarmMessageChannel", new WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm(true, inviteUser.getId()));
                    return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, inviteUser, workspace));
                } else {
                    throw new CustomException(ErrorCode.ALREADY_INVITED);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.Invite> getInvite(User user, Long id) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        List<WorkspaceResponseDto.Invite> inviteList = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByWorkspaceOrderByIdDesc(workspace);

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteList.add(new WorkspaceResponseDto.Invite(workspaceInvite));
        }

        return inviteList;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> cancelInvite(User user, Long id, Long inviteId) {
        Workspace workspace = validateWorkspace(id);
        checkRole(user, workspace);

        WorkspaceInvite workspaceInvite = workspaceInviteRepository.findByWorkspace_IdAndId(id, inviteId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        User inviteUser = workspaceInvite.getUser();
        workspaceInviteRepository.deleteById(inviteId);

        if (inviteUser != null && workspaceInviteRepository.countByEmail(user.getEmail()) == 0) {
            redisTemplate.convertAndSend("workspaceInviteAlarmMessageChannel", new WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm(false, inviteUser.getId()));
        }

        return SendMessageDto.toResponseEntity(SuccessCode.CANCEL_INVITE);
    }

    private Workspace validateWorkspace(Long id) {
        return workspaceRepository.findByIdAndIsShowTrue(id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }

    private WorkspaceUser checkRole(User user, Workspace workspace) {
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        if (workspaceUser.getIsShow() && workspaceUser.getRole() == WorkspaceUserRole.MEMBER) {
            throw new CustomException(ErrorCode.PERMISSION_DINED);
        }

        return workspaceUser;
    }
}