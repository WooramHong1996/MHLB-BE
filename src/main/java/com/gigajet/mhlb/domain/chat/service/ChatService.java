package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.status.repository.SqlStatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;
    private final SqlStatusRepository statusRepository;

    private final JwtUtil jwtUtil;
    private static Long chatId = 0l;

    private final ConcurrentHashMap<String, Integer> endpointMap = new ConcurrentHashMap<>();
    private final int full = 2;

    @Transactional
    public ChatResponseDto.Chat sendMsg(ChatRequestDto.Chat message, String email) {
        Long id = userRepository.findByEmail(email).get().getId();
        Chat chat = Chat.builder()
                .senderId(id)
                .inBoxId(message.getUuid())
                .workspaceId(message.getWorkspaceId())
                .message(message.getMessage())
                .messageId(chatId)
                .build();
        chatId++;
        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());
        chat.setCreatedAt(LocalDateTime.now());
        if (full == endpointMap.get("/sub/inbox/" + message.getUuid())) {
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
                if (id == userAndMessage.getUserId()) {
                    continue;
                }
                userAndMessage.addUnread();
            }
        }
        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);
        chatRepository.save(chat);
        return new ChatResponseDto.Chat(chat);
    }

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

    public List<ChatResponseDto.Inbox> getInbox(User user, Long workspaceId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<ChatResponseDto.Inbox> response = new ArrayList<>();
        List<ChatRoom> list = chatRoomRepository.findByWorkspaceIdAndUserSetInOrderByLastChat(workspaceId, user.getId());
        //user 요청자
        ChatResponseDto.Inbox inbox = new ChatResponseDto.Inbox();
        for (ChatRoom chatRoom : list) {
            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {//리스트대로 돌림
                if (user.getId() == userAndMessage.getUserId()) {
                    inbox.unreadMessage(userAndMessage.getUnread());
                    continue;
                }
                Optional<User> opponents = userRepository.findById(userAndMessage.getUserId());
                inbox.inbox(chatRoom, opponents.get(), statusRepository.findTopByUserIdOrderByUpdateDayDescUpdateTimeDesc(userAndMessage.getUserId()).getStatus().getColor());
            }
            response.add(inbox);
        }
        return response;
    }

    public List<ChatResponseDto.Chat> getChat(User user, Long workspaceId, Long opponentsId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        List<ChatResponseDto.Chat> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);

        List<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId());
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chat(chat));
        }
        return chatList;
    }

    public String resolveTocken(String authorization) {
        return jwtUtil.getUserEmail(authorization.substring(7));
    }

    public void readMessages(StompHeaderAccessor accessor) {
        String email = resolveTocken(accessor.getFirstNativeHeader("Authorization"));

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

    public void subcribe(String endpoint) {
        if (endpointMap.get(endpoint) == null) {
            endpointMap.put(endpoint, 1);
        }
        endpointMap.put(endpoint, endpointMap.get(endpoint) + 1);
    }

    public void unSubcribe(String endpoint) {
        endpointMap.put(endpoint, endpointMap.get(endpoint) - 1);
    }
}