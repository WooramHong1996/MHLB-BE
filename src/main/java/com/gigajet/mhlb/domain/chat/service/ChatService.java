package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.redis.RedisRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.gigajet.mhlb.exception.ErrorCode.WRONG_CHAT_ROOM_ID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;
    private final SqlStatusRepository statusRepository;
    private final RedisRepository redisRepository;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;


    private final JwtUtil jwtUtil;
    private static Long chatId = 0L;

    private final ConcurrentHashMap<String, Integer> endpointMap = new ConcurrentHashMap<>();
    private final int full = 2;

    public void enter(String uuid, String email) {
        Long id = userRepository.findByEmail(email).get().getId();

        // 그룹채팅은 해시코드가 존재하지 않고 일대일 채팅은 해시코드가 존재한다.
        ChatRoom chatRoom = chatRoomRepository.findById(uuid).orElseThrow(() -> new CustomException(ErrorCode.WRONG_CHAT_ROOM_ID));

        // 채팅방에 들어온 정보를 Redis 저장
        redisRepository.userEnterRoomInfo(id, uuid);

        // 그룹채팅은 해시코드가 존재하지 않고 일대일 채팅은 해시코드가 존재한다.
            redisRepository.initChatRoomMessageInfo(chatRoom.getId()+"", id);
    }

    @Transactional
    public ChatResponseDto.Chat sendMsg(ChatRequestDto.Chat message, String email) {
        //탈퇴한 사람 메시지 보낼 수 없도록 막는 로직 구현 필요
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
        //해당 엔드포인트의 구독자가 full의 수와 같지 않은 경우 읽지 않은 메시지를 +1하는 로직
        if (full == endpointMap.get("/sub/inbox/" + message.getUuid())) {
            //이여야 했는데 부호를 반대로 썼음 -> 왜 되는지 모르는 상태임 //수정필요
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

    @Transactional
    public void sendMessage(ChatRequestDto.Chat message, String email) {
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

        chatRepository.save(chat);
        message.setSenderId(chat.getSenderId());
        message.setMessageId(chatId);
        message.setType(ChatRequestDto.MessageType.TALK);
        String topic = channelTopic.getTopic();

            // 일대일 채팅 이면서 안읽은 메세지 업데이트
            redisTemplate.convertAndSend(topic, message);
            updateUnReadMessageCount(message, id);
    }

    //안읽은 메세지 업데이트
    private void updateUnReadMessageCount(ChatRequestDto.Chat message, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(message.getUuid());
        HashSet<Long> userIds = chatRoom.getUserSet();
        Long otherUserId=0L;
        for (long userId: userIds) {
            if(userId != id){
                otherUserId = userId;
            }
        }
        String roomId = String.valueOf(message.getUuid());

        if (!redisRepository.existChatRoomUserInfo(otherUserId) || !redisRepository.getUserEnterRoomId(otherUserId).equals(roomId)) {

            redisRepository.addChatRoomMessageCount(roomId, otherUserId);
            int unReadMessageCount = redisRepository.getChatRoomMessageCount(roomId+"", otherUserId);

            String topic = channelTopic.getTopic();

            ChatRequestDto.Chat messageRequest = new ChatRequestDto.Chat(message, unReadMessageCount);

            redisTemplate.convertAndSend(topic, messageRequest);
        }
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

    @Transactional
    public List<ChatResponseDto.Chat> getChat(User user, Long workspaceId, Long opponentsId) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        List<ChatResponseDto.Chat> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);
        if (chatRoom == null) {
            return new ArrayList<>();
        }

        List<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId());
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chat(chat));
        }
        return chatList;
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

    public void subscribe(String endpoint) {
        if (endpointMap.get(endpoint) == null) {
            endpointMap.put(endpoint, 1);
        }
        endpointMap.put(endpoint, endpointMap.get(endpoint) + 1);
    }

    public void unSubscribe(String endpoint) {
        endpointMap.put(endpoint, endpointMap.get(endpoint) - 1);
    }
}