package com.gigajet.mhlb.domain.chat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@Document(collection = "chat_room")
@NoArgsConstructor
public class ChatRoom {
    @Id
    private String id;

    private List<Long> userList;

    private Long workspaceId;

    private String inBoxId;

    @Builder
    public ChatRoom(List<Long> userList, Long workspaceId, String inBoxId) {
        this.userList = userList;
        this.workspaceId = workspaceId;
        this.inBoxId = inBoxId;
    }
}
