package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
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
import java.util.HashMap;
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
    private final SqlStatusRepository statusRepository;

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

        Long count = workspaceUserRepository.countByUserAndIsShow(user, 1);

        WorkspaceUser workspaceUser = new WorkspaceUser(user, workspace, ADMIN);
        WorkspaceOrder workspaceOrder = new WorkspaceOrder(count, workspaceUser);

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
        if (workspaceUser.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }

        return new WorkspaceResponseDto.InfoAndRoll(workspaceUser.get().getWorkspace(), workspaceUser.get().getRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> changeOrder(User user, WorkspaceRequestDto.Orders orders) {
        List<WorkspaceOrder> orderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        HashMap<Long, Long> orderMap = new HashMap<>();//워크스페이스 id , 순서
        for (WorkspaceRequestDto.Order order : orders.getOrders()) {
            orderMap.put(order.getWorkspaceId(), order.getOrdernum());
        }

        for (WorkspaceOrder workspaceOrder : orderList) {
            workspaceOrder.updateOrder(orderMap.get(workspaceOrder.getWorkspaceUser().getWorkspace().getId()));
            workspaceOrderRepository.save(workspaceOrder);
        }

        return SendMessageDto.toResponseEntity(SuccessCode.ORDER_CHANGE_SUCCESS);
    }

    @Transactional
    public Optional<User> invite(User user, Long id, String email) {
        Optional<WorkspaceUser> workspace = getWorkspaceuser(user, id);

        Optional<WorkspaceInvite> checkInvite = workspaceInviteRepository.findByWorkspaceAndEmail(workspace.get().getWorkspace(), email);
        if (checkInvite.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_INVITED);//기존에 초대 한 사람인지 확인
        }

        Optional<User> invited = userRepository.findByEmail(email);//해당 유저가 회원가입 되어있는지 먼저 확인

        if (invited.isEmpty()) {//회원가입 되어있지 않으면 그대로 반환 후 메일서비스로 넘김
            WorkspaceInvite workspaceInvite = new WorkspaceInvite(email, workspace.get().getWorkspace());
            workspaceInviteRepository.save(workspaceInvite);
            return invited;
        }

        Optional<WorkspaceUser> workspaceUser = workspaceUserRepository.findByUserAndWorkspace(invited.get(), workspace.get().getWorkspace());
        if (workspaceUser.isPresent()) {//워크스페이스에 가입된 유저인지 확인
            throw new CustomException(ErrorCode.WRONG_USER);
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
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByWorkspaceOrderByIdDesc(workspaceUser.get().getWorkspace());

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteList.add(new WorkspaceResponseDto.Invite(workspaceInvite));
        }

        return inviteList;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteInvite(User user, Long id, Long inviteid) {
        Optional<WorkspaceUser> workspaceUser = getWorkspaceuser(user, id);
        checkrole(workspaceUser);

        Optional<WorkspaceInvite> workspaceInvite = workspaceInviteRepository.findByWorkspace_IdAndId(id, inviteid);
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

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.OrderList> getOrder(User user) {
        List<WorkspaceResponseDto.OrderList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            orderLists.add(new WorkspaceResponseDto.OrderList(workspaceOrder.getWorkspaceUser().getWorkspace()));
        }

        return orderLists;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.People> getPeople(User user, Long id) {
        getWorkspaceuser(user, id);

        List<WorkspaceResponseDto.People> peopleList = new ArrayList<>();
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        for (WorkspaceUser workspaceUser : workspaceUserList) {
            SqlStatus status = statusRepository.findTopByUserOrderByUpdatedAtDesc(workspaceUser.getUser());
            peopleList.add(new WorkspaceResponseDto.People(status));
        }

        return peopleList;
    }
}
