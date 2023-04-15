package com.gigajet.mhlb.global.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusSubscriber implements MessageListener {

    private final WorkspaceUserRepository workspaceUserRepository;

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            StatusResponseDto.StatusInfo statusInfo = objectMapper.readValue(publishMessage, StatusResponseDto.StatusInfo.class);
            List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findAllByUser_IdAndIsShowTrue(statusInfo.getUserId());

            for (WorkspaceUser workspaceUser : workspaceUserList) {
                Long workspaceId = workspaceUser.getWorkspace().getId();

                messagingTemplate.convertAndSend("/sub/status/" + workspaceId, statusInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}
