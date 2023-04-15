package com.gigajet.mhlb.domain.status.service;

import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.status.repository.StatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    private final RedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public List<StatusResponseDto.StatusInfo> getWorkspacePeople(User user, Long workspaceId) {
        Workspace workspace = workspaceRepository.findByIdAndIsShowTrue(workspaceId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        List<StatusResponseDto.StatusInfo> responseDto = new ArrayList<>();

        List<WorkspaceUser> byWorkspaceId = workspaceUserRepository.findByWorkspaceAndIsShow(workspace, true);

        for (WorkspaceUser workspaceUser : byWorkspaceId) {
            responseDto.add(new StatusResponseDto.StatusInfo(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(workspaceUser.getUser())));
        }

        return responseDto;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> register(User user) {
        Status status = new Status(user);

        statusRepository.save(status);

        return SendMessageDto.toResponseEntity(SuccessCode.SIGNUP_SUCCESS);
    }

    @Transactional(readOnly = true)
    public StatusResponseDto.StatusInfo myStatus(User user) {
        return new StatusResponseDto.StatusInfo(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user));
    }

    @Transactional
    public void SocketStatusUpdate(String statusName, StompHeaderAccessor accessor) {
        User user = userRepository.findById(Long.valueOf(accessor.getFirstNativeHeader("userId"))).orElseThrow();

        StatusEnum beforeStatusEnum = statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user).getStatus();
        log.info(beforeStatusEnum.getStatus());

        StatusEnum changeStatusEnum = StatusEnum.valueOfStatus(statusName).orElseThrow();
        log.info(changeStatusEnum.getStatus());
        if (beforeStatusEnum == changeStatusEnum) {
            throw new CustomException(ErrorCode.STATUS_NOT_CHANGED);
        }

        Status status = new Status(user, changeStatusEnum);

        statusRepository.save(status);

        StatusResponseDto.StatusInfo convert = new StatusResponseDto.StatusInfo(status);

        redisTemplate.convertAndSend("statusMessageChannel", convert);
    }
}
