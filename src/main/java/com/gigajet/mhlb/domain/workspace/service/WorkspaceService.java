package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceInviteRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceOrderRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole.ADMIN;
import static com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUserRole.MEMBER;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;
    private final S3Handler s3Handler;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceOrderRepository workspaceOrderRepository;

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.AllList> workspaceAllList(User user) {

        List<WorkspaceResponseDto.AllList> allLists = new ArrayList<>();

        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByUserAndIsShow(user, 1);

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            allLists.add(new WorkspaceResponseDto.AllList(workspaceUser.getWorkspace()));
        }

        return allLists;
    }

    @Transactional
    public WorkspaceResponseDto.CreateResponse workspaceCreate(User user, MultipartFile image, WorkspaceRequestDto.Create workspaceDto) throws IOException {
        String imageUrl = s3Handler.upload(image);
        Workspace workspace = new Workspace(workspaceDto, imageUrl);

        Integer count = workspaceUserRepository.countByUserAndIsShow(user, 1);

        WorkspaceOrder workspaceOrder = new WorkspaceOrder(count);
        WorkspaceUser workspaceUser = new WorkspaceUser(user, workspace, ADMIN, workspaceOrder);


        workspaceRepository.save(workspace);

        workspaceUserRepository.save(workspaceUser);

        workspaceOrderRepository.save(workspaceOrder);

        return new WorkspaceResponseDto.CreateResponse(workspace);
    }

    public List inboxGet(User user, Long id, Integer size) {
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto.InfoAndRoll infoAndRoll(User user, Long id) {
        Optional<WorkspaceUser> workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, id);

        return new WorkspaceResponseDto.InfoAndRoll(workspaceUser.get().getWorkspace(), workspaceUser.get().getRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> changeOrder(User user, WorkspaceRequestDto.Orders orders) {
        return SendMessageDto.toResponseEntity(SuccessCode.ORDER_CHANGE_SUCCESS);
    }

    @Transactional
    public Optional<User> invite(User user, Long id, String email) {
        Optional<WorkspaceUser> workspace = getWorkspaceuser(user, id);

        Optional<User> invited = userRepository.findByEmail(email);//해당 유저가 회원가입 되어있는지 먼저 확인

        if (invited.isEmpty()) {//회원가입 되어있지 않으면 그대로 반환 후 메일서비스로 넘김
            WorkspaceInvite workspaceInvite = new WorkspaceInvite(email, workspace.get().getWorkspace());
            workspaceInviteRepository.save(workspaceInvite);
            return invited;
        }

        Optional<WorkspaceInvite> checkInvite = workspaceInviteRepository.findByWorkspaceAndEmail(workspace.get().getWorkspace(), email);
        if (checkInvite.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_INVITED);
        }

        WorkspaceInvite workspaceInvite = new WorkspaceInvite(email, invited.get(), workspace.get().getWorkspace());
        workspaceInviteRepository.save(workspaceInvite);
        return invited;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.Invite> getInvite(User user, Long id) {
        Optional<WorkspaceUser> workspaceUser = getWorkspaceuser(user, id);
        checkrole(workspaceUser);

        List<WorkspaceResponseDto.Invite> inviteList = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByWorkspace(workspaceUser.get().getWorkspace());

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteList.add(new WorkspaceResponseDto.Invite(workspaceInvite));
        }

        return inviteList;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteInvite(User user, Long id, Long inviteid) {
        Optional<WorkspaceUser> workspaceUser = getWorkspaceuser(user, id);
        checkrole(workspaceUser);

        Optional<WorkspaceInvite> workspaceInvite = workspaceInviteRepository.findByWorkspace_IdAndId(workspaceUser.get().getWorkspace(), inviteid);
        if (workspaceInvite.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }

        workspaceInviteRepository.deleteById(inviteid);

        return SendMessageDto.toResponseEntity(SuccessCode.CANCLE_INVITE);
    }

    private Optional<WorkspaceUser> getWorkspaceuser(User user, Long id) {
        Optional<WorkspaceUser> workspace = workspaceUserRepository.findByUserAndWorkspaceId(user, id);//유저가 워크스페이스에 가입 되어있는지 확인
        if (workspace.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }
        return workspace;
    }

    private void checkrole(Optional<WorkspaceUser> workspaceUser) {
        if (workspaceUser.get().getRole() == MEMBER) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
    }
}
