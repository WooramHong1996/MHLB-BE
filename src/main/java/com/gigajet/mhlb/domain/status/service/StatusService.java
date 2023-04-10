package com.gigajet.mhlb.domain.status.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final SqlStatusRepository statusRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final RedisTemplate redisTemplate;

    @Transactional
    public StatusResponseDto statusUpdate(User user, StatusRequestDto statusRequestDto) {
        if (statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user).getStatus().equals(statusRequestDto.textOf())) {
            throw new CustomException(ErrorCode.STATUS_NOT_CHANGED);
        }

        SqlStatus status = new SqlStatus(user, statusRequestDto);

        statusRepository.save(status);

        return new StatusResponseDto(status);
    }

    @Transactional(readOnly = true)
    public List<StatusResponseDto> getWorkspacePeople(User user, Long id) {
        List<StatusResponseDto> responseDto = new ArrayList<>();

        workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        List<WorkspaceUser> byWorkspaceId = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        for (WorkspaceUser workspaceUser : byWorkspaceId) {
            responseDto.add(new StatusResponseDto(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(workspaceUser.getUser())));
        }

        return responseDto;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> register(User user) {
        SqlStatus status = new SqlStatus(user);

        statusRepository.save(status);

        return SendMessageDto.toResponseEntity(SuccessCode.SIGNUP_SUCCESS);
    }

    @Transactional(readOnly = true)
    public StatusResponseDto myStatus(User user) {
        return new StatusResponseDto(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user));
    }

    @Transactional(readOnly = true)
    public List<Long> getWorkspaceList(User user) {
        List<WorkspaceUser> list = workspaceUserRepository.findByUserAndIsShow(user, 1);
        List<Long> longList = new ArrayList<>();
        for (WorkspaceUser workspaceUser : list) {
            longList.add(workspaceUser.getWorkspace().getId());
        }
        return longList;
    }

    public void checkUser(User user, Long id) {
        workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }

    @Transactional
    public void SocketStatusUpdate(StatusRequestDto statusRequestDto, String authorization) {
        Optional<User> user = userRepository.findByEmail(jwtUtil.getUserEmail(authorization.substring(7)));
        if (statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user.get()).getStatus().equals(statusRequestDto.textOf())) {
            throw new CustomException(ErrorCode.STATUS_NOT_CHANGED);
        }

        SqlStatus status = new SqlStatus(user.get(), statusRequestDto);

        statusRepository.save(status);

        List<WorkspaceUser> workspaces = workspaceUserRepository.findByUserAndIsShow(user.get(), 1);

        StatusResponseDto.Convert convert = new StatusResponseDto.Convert(status, workspaces);


        redisTemplate.convertAndSend("statusMessageChannel", convert);
    }
}
