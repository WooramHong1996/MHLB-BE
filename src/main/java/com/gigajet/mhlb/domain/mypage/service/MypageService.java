package com.gigajet.mhlb.domain.mypage.service;

import com.gigajet.mhlb.domain.alarm.dto.WorkspaceInviteAlarmResponseDto;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.S3Handler;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
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
public class MypageService {

    private final UserRepository userRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceOrderRepository workspaceOrderRepository;

    private final S3Handler s3Handler;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${user.default.image}")
    private String defaultImage;

    @Transactional(readOnly = true)
    public MypageResponseDto.Info findUserInfo(User user) {
        return new MypageResponseDto.Info(user);
    }

    @Transactional(readOnly = true)
    public MypageResponseDto.AllList findWorkspaceInfo(User user) {
        List<MypageResponseDto.InviteList> inviteLists = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByUser(user);

        List<MypageResponseDto.WorkspaceList> workspaceLists = new ArrayList<>();
        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByUserAndIsShowTrueOrderByRoleDesc(user);

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteLists.add(new MypageResponseDto.InviteList(workspaceInvite.getWorkspace()));
        }

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            workspaceLists.add(new MypageResponseDto.WorkspaceList(workspaceUser.getWorkspace(), workspaceUser.getWorkspaceOrder().getWorkspaceUser()));
        }

        workspaceLists.sort((o1, o2) -> {
            if (o1.getUserRole() == o2.getUserRole() || o1.getUserRole() != WorkspaceUserRole.ADMIN && o2.getUserRole() != WorkspaceUserRole.ADMIN) {
                return o1.getWorkspaceTitle().toLowerCase().compareTo(o2.getWorkspaceTitle().toLowerCase());
            }
            return Integer.compare(o2.getUserRole().ordinal(), o1.getUserRole().ordinal());
        });

        return new MypageResponseDto.AllList(inviteLists, workspaceLists);
    }

    public MypageResponseDto.Image changeImage(User user, MultipartFile userImage) throws IOException {
        String imageUrl = s3Handler.upload(userImage);
        if (!user.getImage().equals(defaultImage)) {
            s3Handler.delete(user.getImage());
        }

        userRepository.updateImage(imageUrl, user.getId());

        return new MypageResponseDto.Image(imageUrl);
    }

    public MypageResponseDto.Name changeName(User user, String name) {
        userRepository.updateUserName(name, user.getId());
        return new MypageResponseDto.Name(name);
    }

    public MypageResponseDto.Description changeDesc(User user, String description) {
        userRepository.updateDescription(description, user.getId());
        return new MypageResponseDto.Description(description);
    }

    public MypageResponseDto.Job changeJob(User user, String job) {
        userRepository.updateJob(job, user.getId());
        return new MypageResponseDto.Job(job);
    }

    @Transactional
    public ResponseEntity<SendMessageDto> leaveWorkspace(User user, long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
        workspaceUser.offIsShow();

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.DELETE_SUCCESS));
    }

    @Transactional
    public ResponseEntity<SendMessageDto> acceptWorkspaceInvite(User user, long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);
        workspaceInviteRepository.findByWorkspace_IdAndUserId(workspaceId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        Optional<WorkspaceUser> alreadyExist = workspaceUserRepository.findByUserAndWorkspaceAndIsShow(user, workspace, false);

        if (alreadyExist.isPresent()) {
            alreadyExist.get().onIsShow();
            workspaceOrderRepository.findByWorkspaceUserAndIsShow(alreadyExist.get(), false).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER)).onIsShow();
        } else {
            WorkspaceUser workspaceUser = new WorkspaceUser(user, workspace);
            Long count = workspaceUserRepository.countByUserAndIsShow(user, true);

            WorkspaceOrder workspaceOrder = new WorkspaceOrder(count, workspaceUser);

            workspaceUserRepository.save(workspaceUser);
            workspaceOrderRepository.save(workspaceOrder);

            return ResponseEntity.ok(SendMessageDto.of(SuccessCode.INVITE_SUCCESS));
        }

        workspaceInviteRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);

        if (workspaceInviteRepository.findByUser(user).isEmpty()) {
            redisTemplate.convertAndSend("workspaceInviteAlarmMessageChannel", new WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm(false, user.getId()));
        }

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.INVITE_SUCCESS));
    }

    @Transactional
    public ResponseEntity<SendMessageDto> rejectWorkspaceInvite(User user, long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);

        workspaceInviteRepository.findByWorkspace_IdAndUserId(workspaceId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
        workspaceInviteRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);

        if (workspaceInviteRepository.findByUser(user).isEmpty()) {
            redisTemplate.convertAndSend("workspaceInviteAlarmMessageChannel", new WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm(false, user.getId()));
        }

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.INVITE_REJECT));
    }

    private Workspace validateWorkspace(Long id) {
        return workspaceRepository.findByIdAndIsShowTrue(id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }
}