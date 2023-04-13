package com.gigajet.mhlb.global.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StatusSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final WorkspaceUserRepository workspaceUserRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            StatusResponseDto.Convert status = objectMapper.readValue(publishMessage, StatusResponseDto.Convert.class);

            List<WorkspaceUser> workspaces = workspaceUserRepository.findByUser_IdAndIsShowTrue(status.getUserId());

            StatusResponseDto responseDto = new StatusResponseDto(status);

            for (WorkspaceUser workspaceUser : workspaces) {
                messagingTemplate.convertAndSend("/sub/status/" + workspaceUser.getWorkspace().getId(), responseDto);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}
