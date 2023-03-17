package com.gigajet.mhlb.domain.status.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.dto.StatusRequestDto;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.status.repository.StatusRepository;
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
    private final StatusRepository statusRepository;
    private final WorkspaceUserRepository workspaceUserRepository;

    public String statusUpdate(User user, StatusRequestDto statusRequestDto) {
        Status status = statusRepository.findByEmail(user.getEmail());

        status.update(statusRequestDto);

        statusRepository.save(status);

        return status.getStatus();
    }

    public List getWorkspacePeople(User user, Long id) {
        List<StatusResponseDto> responseDto = new ArrayList<>();

        workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        List<WorkspaceUser> byWorkspaceId = workspaceUserRepository.findByWorkspace_Id(id);

        for (WorkspaceUser workspaceUser : byWorkspaceId) {
            Status status = statusRepository.findByEmail(workspaceUser.getUser().getEmail());
            responseDto.add(new StatusResponseDto(status));
        }

        return responseDto;
    }

    public ResponseEntity<SendMessageDto> register(UserRequestDto.Register registerDto) {
        Status status = new Status(registerDto.getEmail());

        statusRepository.save(status);

        return SendMessageDto.toResponseEntity(SuccessCode.SIGNUP_SUCCESS);
    }

    public StatusResponseDto myStatus(User user) {
        Status status = statusRepository.findByEmail(user.getEmail());

        StatusResponseDto response = new StatusResponseDto(status);

        return response;
    }
}
