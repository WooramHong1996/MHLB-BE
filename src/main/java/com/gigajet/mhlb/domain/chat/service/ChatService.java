package com.gigajet.mhlb.domain.chat.service;

import com.gigajet.mhlb.domain.chat.dto.ChatRequestDto;
import com.gigajet.mhlb.domain.chat.dto.ChatResponse;
import com.gigajet.mhlb.domain.chat.entity.Chat;
import com.gigajet.mhlb.domain.chat.entity.ChatRoom;
import com.gigajet.mhlb.domain.chat.repository.ChatRepository;
import com.gigajet.mhlb.domain.chat.repository.ChatRoomRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendMsg(ChatRequestDto.Chat message) {
        Chat chat = Chat.builder()
                .inBoxId(message.getUuid())
                .senderId(message.getSenderId())
                .workspaceId(message.getWorkspaceId())
                .message(message.getMessage())
                .build();

        ChatRoom chatRoom = chatRoomRepository.findByInBoxId(chat.getInBoxId());
        chat.setCreatedAt(LocalDateTime.now());
        chatRoom.update(chat);
        chatRoomRepository.save(chatRoom);
        chatRepository.save(chat);
    }

    public ChatResponse.GetUuid getUuid(User user, Long workspaceId, Long opponentsId) {
        if (user.getId() == opponentsId) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(user.getId(), workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));
        workspaceUserRepository.findByUser_IdAndWorkspace_IdAndIsShow(opponentsId, workspaceId, 1).orElseThrow(() -> new CustomException(ErrorCode.WRONG_USER));

        List<Long> userIdList = new ArrayList<>();
        if (user.getId() > opponentsId) {
            userIdList.add(user.getId());
            userIdList.add(opponentsId);
        } else {
            userIdList.add(opponentsId);
            userIdList.add(user.getId());
        }

        ChatRoom chatRoom = chatRoomRepository.findByUserListAndWorkspaceId(userIdList, workspaceId);
        if (chatRoom == null) {
            String inboxId = String.valueOf(UUID.randomUUID());

            ChatRoom inbox = ChatRoom.builder()
                    .inBoxId(inboxId)
                    .userList(userIdList)
                    .workspaceId(workspaceId)
                    .build();
            chatRoomRepository.save(inbox);
            return new ChatResponse.GetUuid(inbox.getInBoxId());
        }
        return new ChatResponse.GetUuid(chatRoom.getInBoxId());
    }

    public List<ChatResponse.Inbox> getInbox(User user, Long workspaceId) {
        List<ChatResponse.Inbox> response = new ArrayList<>();
        List<ChatRoom> list = chatRoomRepository.findByWorkspaceIdAndUserListInOrderByLastChatDesc(workspaceId, user.getId());

        for (ChatRoom chatRoom : list) {
            for (Long aLong : chatRoom.getUserList()) {
                if(user.getId()==aLong){
                    continue;
                }
                Optional<User> opponents = userRepository.findById(aLong);
                response.add(new ChatResponse.Inbox(chatRoom,opponents.get(),0));
            }
        }

        return response;
    }
}