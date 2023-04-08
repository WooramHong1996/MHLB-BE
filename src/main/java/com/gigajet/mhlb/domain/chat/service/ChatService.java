package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.MessageId;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.chat.repository.MessageIdRepository;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;
    private final SqlStatusRepository statusRepository;
    private final MessageIdRepository messageIdRepository;

    private final RedisTemplate redisTemplate;

    private final JwtUtil jwtUtil;

    @Transactional
    public void sendMsg(ChatRequestDto.Chat message, String email) {
        MessageId messageId = messageIdRepository.findTopByKey(1);
        messageId.addMessageId();
//        MessageId messageId = new MessageId(1L);
        messageIdRepository.save(messageId);

        Long id = userRepository.findByEmail(email).get().getId();

        Chat chat = Chat.builder()
                .senderId(id)
                .inBoxId(message.getUuid())
                .workspaceId(message.getWorkspaceId())
                .message(message.getMessage())
                .messageId(messageId.getMessageId())
                .build();
        chat.setCreatedAt(LocalDateTime.now());

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());
        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);

        chatRepository.save(chat);

        redisTemplate.convertAndSend("chatMessageChannel", new ChatResponseDto.Convert(chat));
    }

    @Transactional
    public List<ChatResponseDto.Chatting> getChat(User user, Long workspaceId, Long opponentsId, Pageable pageable) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Chatting> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);
        if (chatRoom == null) {
            return new ArrayList<>();
        }

        Slice<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId(), pageable);
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chatting(chat));
        }

        Collections.sort(chatList, (a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        return chatList;
    }

    @Transactional
    public ChatResponseDto.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<UserAndMessage> userIdList = new ArrayList<>();
        userIdList.add(new UserAndMessage(user.getId()));
        userIdList.add(new UserAndMessage(opponentsId));

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);

        if (chatRoom == null) {
            String inboxId = String.valueOf(UUID.randomUUID());

            ChatRoom inbox = ChatRoom.builder()
                    .inBoxId(inboxId)
                    .userAndMessages(userIdList)
                    .userSet(userIdSet)
                    .workspaceId(workspaceId)
                    .build();
            chatRoomRepository.save(inbox);
            return new ChatResponseDto.GetUuid(inbox.getInBoxId());
        }
        return new ChatResponseDto.GetUuid(chatRoom.getInBoxId());
    }

    @Transactional
    public List<ChatResponseDto.Inbox> getInbox(User user, Long workspaceId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Inbox> response = new ArrayList<>();
        List<ChatRoom> list = chatRoomRepository.findByWorkspaceIdAndUserSetInOrderByLastChatDesc(workspaceId, user.getId());
        //user 요청자

        for (ChatRoom chatRoom : list) {
            ChatResponseDto.Inbox inbox = new ChatResponseDto.Inbox();
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {//리스트대로 돌림
                if (user.getId() == userAndMessage.getUserId()) {
                    inbox.unreadMessage(userAndMessage.getUnread());
                    continue;
                }
                Optional<User> opponents = userRepository.findById(userAndMessage.getUserId());
                //퇴사한 유저인지 확인하여 퇴사한 경우 채팅 보낼 수 없도록 기능 수정 필요
                inbox.inbox(chatRoom, opponents.get(), statusRepository.findTopByUserIdOrderByUpdateDayDescUpdateTimeDesc(userAndMessage.getUserId()).getStatus().getColor());
            }
            response.add(inbox);
        }
        return response;
    }

    public String resolveToken(String authorization) {
        return jwtUtil.getUserEmail(authorization.substring(7));
    }

    @Transactional
    public void readMessages(StompHeaderAccessor accessor) {
        String email = resolveToken(accessor.getFirstNativeHeader("Authorization"));

        Long id = userRepository.findByEmail(email).get().getId();

        String uuid = accessor.getFirstNativeHeader("uuid");

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(uuid);

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (id != userAndMessage.getUserId()) {
                continue;
            }
            userAndMessage.resetUnread();
        }
        chatRoomRepository.save(chatRoom);
    }

    public void checkRoom(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        Long userId = userRepository.findByEmail(resolveToken(accessor.getFirstNativeHeader("Authorization"))).get().getId();
        //여기서 입장 및 퇴장 로직을 구성 할 예정 입니다.
        System.out.println(destination);
    }
}