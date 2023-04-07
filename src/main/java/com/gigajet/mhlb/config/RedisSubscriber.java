package com.gigajet.mhlb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.dto.UnreadMessageCount;
import com.gigajet.mhlb.domain.status.dto.StatusResponseDto;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행되면 대기하고 있던 onMessage가 메시지를 받아 messagingTemplate를 이용하여 websocket 클라이언트들에게 메시지 전달
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            if (message.toString().split("\"")[1].equals("uuid")) {//uuid가 들어올 경우 채팅 기능이므로 분기처리

                // ChatMessage 객체로 맵핑
                ChatRequestDto.Chat roomMessage = objectMapper.readValue(publishMessage, ChatRequestDto.Chat.class);

                // getType이 TALK, GROUP_TALK, UNREAD_MESSAGE_COUNT_ALARM 일 경우
                if (roomMessage.getType() != null) {
//                    if (roomMessage.getType().equals(ChatRequestDto.MessageType.UNREAD_MESSAGE_COUNT_ALARM)) {
//                        // 안 읽은 메세지일 경우
//                        UnreadMessageCount messageCount = new UnreadMessageCount(roomMessage);
//                        Long otherUserId = roomMessage.getOtherUserIds();
//                        // Websocket 구독자에게 안읽은 메세지 반환
//                        messagingTemplate.convertAndSend("/sub/unread/" + otherUserId, messageCount);
//                    } else {
                    // 그룹채팅이거나 일대일 채팅일 경우
                    ChatResponseDto.Chat chatMessageResponse = new ChatResponseDto.Chat(roomMessage);
                    ChatResponseDto.Chating chating = new ChatResponseDto.Chating(chatMessageResponse);
                    // Websocket 구독자에게 채팅 메시지 전송
                    messagingTemplate.convertAndSend("/sub/inbox/" + roomMessage.getUuid(),chating);
//                    }
//            } else {   // 만약 AlarmRequest 클래스로 넘어왔다면
//                AlarmRequest alarmRequest = objectMapper.readValue(publishMessage, AlarmRequest.class);
//                messagingTemplate.convertAndSend("/sub/user/" + alarmRequest.getOtherUserId(), AlarmResponse.toDto(alarmRequest));
                }
            } else {
                StatusResponseDto.Convert status = objectMapper.readValue(publishMessage, StatusResponseDto.Convert.class);

                for (WorkspaceUser workspaceUser : status.getWorkspaces()) {
                    StatusResponseDto responseDto = new StatusResponseDto(status);

                    messagingTemplate.convertAndSend("/sub/status/" + workspaceUser.getWorkspace().getId(), responseDto);
                }

            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}
