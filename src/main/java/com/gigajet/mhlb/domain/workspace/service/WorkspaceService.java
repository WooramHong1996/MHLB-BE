package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.alarm.Entity.Alarm;
import com.gigajet.mhlb.domain.alarm.Repository.AlarmRepository;
import com.gigajet.mhlb.domain.status.entity.SqlStatus;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceOrderRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
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

import static com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceOrderRepository workspaceOrderRepository;
    private final SqlStatusRepository statusRepository;
    private final AlarmRepository alarmRepository;

    private final S3Handler s3Handler;

    @Value("${workspace.default.image}")
    private String defaultImage;

    @Transactional//(readOnly = true)
    public List<WorkspaceResponseDto.AllList> findWorkspaceList(User user) {
        List<WorkspaceResponseDto.AllList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            Optional<Alarm> alarm = alarmRepository.findTopByUserAndWorkspaceIdAndUnreadMessage(user, workspaceOrder.getWorkspaceUser().getWorkspace().getId(), true);
            if (alarm.isEmpty()) {
                orderLists.add(new WorkspaceResponseDto.AllList(workspaceOrder.getWorkspaceUser().getWorkspace(), false));
            } else {
                orderLists.add(new WorkspaceResponseDto.AllList(workspaceOrder.getWorkspaceUser().getWorkspace(), alarm.get().getUnreadMessage()));
            }
        }

        return orderLists;
    }

    //알림 가져오기 메소드 밑으로 빼둠
    @Transactional
    public WorkspaceResponseDto.Response createWorkspace(User user, MultipartFile image, WorkspaceRequestDto.Create workspaceDto) throws IOException {
        String imageUrl;
        if (image == null) {
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

        return new WorkspaceResponseDto.Response(workspace);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto.InfoAndRoll findWorkspaceInfoAndRoll(User user, Long id) {
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
        return new WorkspaceResponseDto.InfoAndRoll(workspaceUser.getWorkspace(), workspaceUser.getRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> changeOrder(User user, WorkspaceRequestDto.Orders orders) {
        List<WorkspaceOrder> orderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        HashMap<Long, Long> orderMap = new HashMap<>();//워크스페이스 id , 순서
        for (WorkspaceRequestDto.Order order : orders.getOrders()) {
            orderMap.put(order.getWorkspaceId(), order.getOrderNum());
        }

        for (WorkspaceOrder workspaceOrder : orderList) {
            workspaceOrderRepository.orderUpdate(orderMap.get(workspaceOrder.getWorkspaceUser().getWorkspace().getId()), workspaceOrder.getWorkspaceUser().getId());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.ORDER_CHANGE_SUCCESS);
    }

    @Transactional//(readOnly = true)
    public List<WorkspaceResponseDto.OrderList> findOrder(User user) {
        List<WorkspaceResponseDto.OrderList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, 1);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            orderLists.add(new WorkspaceResponseDto.OrderList(workspaceOrder.getWorkspaceUser().getWorkspace(), workspaceOrder.getOrders()));
        }

        return orderLists;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.People> findPeople(User user, Long id) {
        workspaceUserRepository.findByUserAndWorkspaceId(user, id).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        List<WorkspaceResponseDto.People> peopleList = new ArrayList<>();
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_IdAndIsShow(id, 1);

        for (WorkspaceUser workspaceUser : workspaceUserList) {
            if (Objects.equals(workspaceUser.getUser().getId(), user.getId())) {
                continue;
            }
            SqlStatus status = statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(workspaceUser.getUser());
            peopleList.add(new WorkspaceResponseDto.People(status));
        }

        peopleList.sort((o1, o2) -> {
            if (Objects.equals(o1.getColor(), o2.getColor())) {
                return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
            }
            return Integer.compare(o1.getColor(), o2.getColor());
        });

        peopleList.add(0, new WorkspaceResponseDto.People(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user)));//본인이 가장 먼저 나오게 해야함

        return peopleList;
    }
}
//
//class StatusComparator implements Comparator<WorkspaceResponseDto.People> {
//    @Override
//    public int compare(WorkspaceResponseDto.People o1, WorkspaceResponseDto.People o2) {
//        if (Objects.equals(o1.getColor(), o2.getColor())) {
//            return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
//        }
//        return Integer.compare(o1.getColor(), o2.getColor());
//    }
//}
