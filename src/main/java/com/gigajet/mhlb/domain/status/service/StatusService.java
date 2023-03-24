package com.gigajet.mhlb.domain.status.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final SqlStatusRepository statusRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    @Transactional
    public StatusResponseDto statusUpdate(User user, StatusRequestDto statusRequestDto) {
        if(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user).getStatus()==statusRequestDto.getStatus()){
            throw new CustomException(ErrorCode.STATUS_NOT_CHANGED);
        }

        SqlStatus status = new SqlStatus(user,statusRequestDto);

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
}
