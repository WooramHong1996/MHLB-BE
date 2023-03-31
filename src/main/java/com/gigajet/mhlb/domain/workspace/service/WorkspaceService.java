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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Value("{workspace.default.image}")
    private String defaultImage;

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
        String imageUrl = "";

        if (image.isEmpty()) {
            imageUrl = defaultImage;
        } else {
            imageUrl = s3Handler.upload(image);
        }

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
            workspaceOrderRepository.orderUpdate(orderMap.get(workspaceOrder.getWorkspaceUser().getWorkspace().getId()), workspaceOrder.getWorkspaceUser().getId());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.ORDER_CHANGE_SUCCESS);
    }

    @Transactional
    public WorkspaceInvite invite(User user, Long id, String invitedUserEmail) {
        WorkspaceUser managerUser = getWorkspaceUser(user, id);

        Optional<WorkspaceInvite> checkInvite = workspaceInviteRepository.findByWorkspaceAndEmail(managerUser.getWorkspace(), invitedUserEmail);
        // 기존에 초대 한 사람인지 확인
        if (checkInvite.isPresent()) {
            return checkInvite.get();
        } else {
            // 해당 유저가 회원가입 되어있는지 먼저 확인
            Optional<User> invitedUser = userRepository.findByEmail(invitedUserEmail);

            if (invitedUser.isEmpty()) {
                return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, managerUser.getWorkspace()));
            } else {
                if (workspaceUserRepository.findByUserAndWorkspace(invitedUser.get(), managerUser.getWorkspace()).isPresent()) {
                    throw new CustomException(ErrorCode.ALREADY_INVITED);
                }

                return workspaceInviteRepository.save(new WorkspaceInvite(invitedUserEmail, invitedUser.get(), managerUser.getWorkspace()));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.Invite> getInvite(User user, Long id) {
        WorkspaceUser workspaceUser = getWorkspaceUser(user, id);
        checkRole(workspaceUser);

        List<WorkspaceResponseDto.Invite> inviteList = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByWorkspaceOrderByIdDesc(workspaceUser.getWorkspace());

        for (WorkspaceInvite workspaceInvite : workspaceInviteList) {
            inviteList.add(new WorkspaceResponseDto.Invite(workspaceInvite));
        }

        return inviteList;
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteInvite(User user, Long id, Long inviteId) {
        WorkspaceUser workspaceUser = getWorkspaceUser(user, id);
        checkRole(workspaceUser);

        Optional<WorkspaceInvite> workspaceInvite = workspaceInviteRepository.findByWorkspace_IdAndId(id, inviteId);
        if (workspaceInvite.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }

        workspaceInviteRepository.deleteById(inviteId);

        return SendMessageDto.toResponseEntity(SuccessCode.CANCLE_INVITE);
    }

    private WorkspaceUser getWorkspaceUser(User user, Long id) {
        Optional<WorkspaceUser> workspace = workspaceUserRepository.findByUserAndWorkspaceId(user, id);//유저가 워크스페이스에 가입 되어있는지 확인
        if (workspace.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }
        return workspace.get();
    }

    private void checkRole(WorkspaceUser workspaceUser) {
        if (workspaceUser.getRole() == MEMBER) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.OrderList> getOrder(User user) {
        List<WorkspaceResponseDto.OrderList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            orderLists.add(new WorkspaceResponseDto.OrderList(workspaceOrder.getWorkspaceUser().getWorkspace(), workspaceOrder.getOrders()));
        }

        return orderLists;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.People> getPeople(User user, Long id) {
        getWorkspaceUser(user, id);

        List<WorkspaceResponseDto.People> peopleList = new ArrayList<>();
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        for (WorkspaceUser workspaceUser : workspaceUserList) {
            if (workspaceUser.getUser().getId() == user.getId()) {
                continue;
            }
            SqlStatus status = statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(workspaceUser.getUser());
            peopleList.add(new WorkspaceResponseDto.People(status));
        }

        peopleList.sort(new StatusComparator());

        peopleList.add(0, new WorkspaceResponseDto.People(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user)));//본인이 가장 먼저 나오게 해야함

        return peopleList;
    }
}

class StatusComparator implements Comparator<WorkspaceResponseDto.People> {
    @Override
    public int compare(WorkspaceResponseDto.People o1, WorkspaceResponseDto.People o2) {
        if (o1.getColor() == o2.getColor()) {
            return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
        }
        return Integer.compare(o1.getColor(), o2.getColor());
    }
}
