package com.gigajet.mhlb.global.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.alarm.dto.WorkspaceInviteAlarmResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkspaceInviteAlarmSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm convertWorkspaceInviteAlarm = objectMapper.readValue(publishMessage, WorkspaceInviteAlarmResponseDto.ConvertWorkspaceInviteAlarm.class);
            messagingTemplate.convertAndSend("/sub/workspace-invite/" + convertWorkspaceInviteAlarm.getUserId(), new WorkspaceInviteAlarmResponseDto.workspaceInviteAlarm(convertWorkspaceInviteAlarm.isInvitedWorkspace()));
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}
