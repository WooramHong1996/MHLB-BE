package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponseDto;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.entity.MessageId;
import com.gigajet.mhlb.domain.chat.entity.UserAndMessage;
import com.gigajet.mhlb.domain.chat.redis.RedisRepository;
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
import org.springframework.data.redis.listener.ChannelTopic;
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
    private final RedisRepository redisRepository;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final MessageIdRepository messageIdRepository;

    private final JwtUtil jwtUtil;
    private final ConcurrentHashMap<String, Integer> endpointMap = new ConcurrentHashMap<>();
    //혹시나 제가 자느라 못오면 이부분만 얘기해 주세요 처음 들어올 때 안읽은 메시지 처리하고, 들어와 있다고 알리는 이벤트에요
    //이부분 api 짜야할거같아요
    public void enter(Long userId, String uuid) {
        // 그룹채팅은 해시코드가 존재하지 않고 일대일 채팅은 해시코드가 존재한다.
        ChatRoom chatRoom = chatRoomRepository.findById(uuid).orElseThrow(() -> new CustomException(ErrorCode.WRONG_CHAT_ROOM_ID));

        // 채팅방에 들어온 정보를 Redis 저장
        redisRepository.userEnterRoomInfo(userId, uuid);

        for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
            if (userId != userAndMessage.getUserId()) {
                continue;
            }
            userAndMessage.resetUnread();
        }
        chatRoomRepository.save(chatRoom);

        // 그룹채팅은 해시코드가 존재하지 않고 일대일 채팅은 해시코드가 존재한다.
        redisRepository.initChatRoomMessageInfo(chatRoom.getId() + "", userId);
    }

//    @Transactional
//    public ChatResponseDto.Chat sendMsg(ChatRequestDto.Chat message, String email) {
//        //탈퇴한 사람 메시지 보낼 수 없도록 막는 로직 구현 필요
//        Long id = userRepository.findByEmail(email).get().getId();
//        Chat chat = Chat.builder()
//                .senderId(id)
//                .inBoxId(message.getUuid())
//                .workspaceId(message.getWorkspaceId())
//                .message(message.getMessage())
//                .messageId(chatId)
//                .build();
//        chatId++;
//        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());
//        chat.setCreatedAt(LocalDateTime.now());
//        //해당 엔드포인트의 구독자가 full의 수와 같지 않은 경우 읽지 않은 메시지를 +1하는 로직
//        if (full == endpointMap.get("/sub/inbox/" + message.getUuid())) {
//            //이여야 했는데 부호를 반대로 썼음 -> 왜 되는지 모르는 상태임 //수정필요
//            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
//                if (id == userAndMessage.getUserId()) {
//                    continue;
//                }
//                userAndMessage.addUnread();
//            }
//        }
//        chatRoom.update(chat);
//        chatRoomRepository.save(chatRoom);
//        chatRepository.save(chat);
//        return new ChatResponseDto.Chat(chat);
//    }

    @Transactional
    public void sendMessage(ChatRequestDto.Chat message, String email) {
//        MessageId messageId = messageIdRepository.getMessageIdByKey(1);
//        messageId.addMessageId();
        //처음에 db넣을 때 이거 쓰시고 넣은 후에 위에 있는 코드 쓰세여
        MessageId messageId = new MessageId(1l);
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

        chatRepository.save(chat);
        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());

        message.setSenderId(chat.getSenderId());
        message.setMessageId(messageId.getMessageId());
        message.setType(ChatRequestDto.MessageType.TALK);
        message.setCreatAt(chat.getCreatedAt().toString());
        String topic = channelTopic.getTopic();

        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);

        // 일대일 채팅 이면서 안읽은 메세지 업데이트
        updateUnReadMessageCount(message, id);
        redisTemplate.convertAndSend(topic, message);
    }

    //안읽은 메세지 업데이트
    private void updateUnReadMessageCount(ChatRequestDto.Chat message, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(message.getUuid());
        HashSet<Long> userIds = chatRoom.getUserSet();
        long otherUserId = 0L;
        for (long userId : userIds) {
            if (userId != id) {
                otherUserId = userId;
            }
        }
        String roomId = String.valueOf(message.getUuid());
        redisRepository.initChatRoomMessageInfo(chatRoom.getId() + "", id);

        if (!redisRepository.existChatRoomUserInfo(otherUserId) || !redisRepository.getUserEnterRoomId(otherUserId).equals(roomId)) {

            redisRepository.addChatRoomMessageCount(roomId, otherUserId);
            Long unReadMessageCount = redisRepository.getChatRoomMessageCount(roomId + "", otherUserId);

            for (UserAndMessage userAndMessage : chatRoom.getUserAndMessages()) {
                if (id == userAndMessage.getUserId()) {
                    continue;
                }
                userAndMessage.addUnread();
            }
            chatRoomRepository.save(chatRoom);

            String topic = channelTopic.getTopic();

            ChatRequestDto.Chat messageRequest = new ChatRequestDto.Chat(message, unReadMessageCount);

//            redisTemplate.convertAndSend(topic, messageRequest);
        }
    }

    @Transactional
    public ChatResponseDto.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);

        if (chatRoom == null) {
            String inboxId = String.valueOf(UUID.randomUUID());

            List<UserAndMessage> userIdList = new ArrayList<>();
            userIdList.add(new UserAndMessage(user.getId(), inboxId));
            userIdList.add(new UserAndMessage(opponentsId, inboxId));

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
    public List<ChatResponseDto.Chating> getChat(User user, Long workspaceId, Long opponentsId, Pageable pageable) {
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        List<ChatResponseDto.Chating> chatList = new ArrayList<>();

        HashSet<Long> userIdSet = new HashSet<>();
        userIdSet.add(user.getId());
        userIdSet.add(opponentsId);

        ChatRoom chatRoom = chatRoomRepository.findByUserSetAndWorkspaceId(userIdSet, workspaceId);
        if (chatRoom == null) {
            return new ArrayList<>();
        }

        Slice<Chat> messageList = chatRepository.findByInBoxId(chatRoom.getInBoxId(), pageable);
        for (Chat chat : messageList) {
            chatList.add(new ChatResponseDto.Chating(chat));
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

//    public void sendChatAlarm(ChatRequestDto.Chat chatMessageRequest, String email) {
//        Long id = userRepository.findByEmail(email).get().getId();
//        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(message.getUuid());
//        HashSet<Long> otherUserId = chatRoom.getUserSet();
//        Long otherUserId=0L;
//        for (long userId: userIds) {
//            if(userId != id){
//                otherUserId = userId;
//            }
//        }
////        String roomId = String.valueOf(message.getUuid());
////        Set<Long> otherUserIds = chatMessageRequest.getOtherUserIds();
//        User user = userRepository.findById(id).orElseThrow(()->new CustomException(WRONG_USER));
//        messageIfExistsOtherUser(chatMessageRequest, user, otherUserId);
//    }

//    private void messageIfExistsOtherUser(ChatRequestDto.Chat req, User user, Long otherUserId) {
//        // 채팅방에 받는 사람이 존재하지 않는다면
//        if (!redisRepository.existChatRoomUserInfo(otherUserId) || !redisRepository.getUserEnterRoomId(otherUserId).equals(req.getRoomId())) {
//            User otherUser = userRepository.findById(otherUserId).orElseThrow(()->new CustomException(WRONG_USER));
//            String topic = channelTopic.getTopic();
//
//            // 그룹, 1:1채팅에 따라 제목 변경
//            System.out.println(user.getUsername());
//            String title =  user.getUsername() + "님이 메시지를 보냈습니다.";
//
//            Alarm alarm = alarmRepository.save(Alarm.builder()
//                    .title(title)
//                    .url("chatURL")
//                    .user(otherUser).build());
//
//            redisTemplate.convertAndSend(topic, AlarmRequest.toDto(alarm, otherUserId));
//        }
//    }
}