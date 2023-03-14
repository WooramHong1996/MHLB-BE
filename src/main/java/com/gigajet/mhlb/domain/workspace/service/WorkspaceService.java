package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceDto;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public List<WorkspaceDto.AllList> workspaceAllList(User user) {
        return new ArrayList<>();
    }

    public ResponseEntity<SendMessageDto> workspaceCreate(User user, MultipartFile image, WorkspaceDto.Create workspaceDto) {
        return null;
    }

    public List inboxGet(User user,Long id, Integer size) {
        return new ArrayList<>();
    }
}
