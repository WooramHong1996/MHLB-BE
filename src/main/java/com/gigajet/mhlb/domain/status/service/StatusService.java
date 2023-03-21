package com.gigajet.mhlb.domain.status.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final SqlStatusRepository statusRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    public StatusResponseDto statusUpdate(User user, StatusRequestDto statusRequestDto) {
        SqlStatus status = statusRepository.findByEmail(user.getEmail());

        status.update(statusRequestDto);

        statusRepository.save(status);

        return new StatusResponseDto(status);
    }

    public List<StatusResponseDto> getWorkspacePeople(User user, Long id) {
        List<StatusResponseDto> responseDto = new ArrayList<>();

        workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        List<WorkspaceUser> byWorkspaceId = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        for (WorkspaceUser workspaceUser : byWorkspaceId) {
            SqlStatus status = statusRepository.findByEmail(workspaceUser.getUser().getEmail());
            responseDto.add(new StatusResponseDto(status));
        }

        return responseDto;
    }

    public ResponseEntity<SendMessageDto> register(UserRequestDto.Register registerDto) {
        SqlStatus status = new SqlStatus(registerDto.getEmail());

        statusRepository.save(status);

        return SendMessageDto.toResponseEntity(SuccessCode.SIGNUP_SUCCESS);
    }

    public StatusResponseDto myStatus(User user) {
        SqlStatus status = statusRepository.findByEmail(user.getEmail());

        return new StatusResponseDto(status);
    }

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
