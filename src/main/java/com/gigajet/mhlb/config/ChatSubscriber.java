package com.gigajet.mhlb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            ChatResponseDto.Convert chat = objectMapper.readValue(publishMessage, ChatResponseDto.Convert.class);
            ChatResponseDto.Chat response = new ChatResponseDto.Chat(chat);

            messagingTemplate.convertAndSend("/sub/inbox/" + chat.getInboxId(), response);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNDEFINED_REQUEST);
        }
    }
}