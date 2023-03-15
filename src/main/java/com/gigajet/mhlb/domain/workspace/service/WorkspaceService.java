package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceUserRepository workspaceUserRepository;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<WorkspaceDto.AllList> workspaceAllList(User user) {

        List<WorkspaceDto.AllList> allLists = new ArrayList<>();

        List<WorkspaceUser> workspaceUsers =workspaceUserRepository.findByUser(user);

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            allLists.add(new WorkspaceDto.AllList(workspaceUser.getWorkspace()));
        }

        return allLists;
    }

    @Transactional
    public WorkspaceDto.CreateResponse workspaceCreate(User user, MultipartFile image, WorkspaceDto.Create workspaceDto) {
        //임시코드 시작 > 구현시 제거
        String imageurl = "더미데이터";
        //임시코드 끝

        Workspace workspace = new Workspace(workspaceDto, imageurl);

        workspaceRepository.save(workspace);

        workspaceUserRepository.save(new WorkspaceUser(user, workspace, ADMIN));

        return new WorkspaceDto.CreateResponse(workspace);
    }

    public List inboxGet(User user, Long id, Integer size) {
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public WorkspaceDto.InfoAndRoll infoAndRoll(User user, Long id) {
        Optional<WorkspaceUser> workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, id);

        return new WorkspaceDto.InfoAndRoll(workspaceUser.get().getWorkspace(), workspaceUser.get().getRole());
    }
}
