package com.example.lineofduty.domain.chatbot.dto.response;

import com.example.lineofduty.domain.chatbot.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminListResponse {
    private Long roomId;
    private Long userId;
    private String username;
    private Long threadCount;
    private Long messageCount;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;

    public static AdminListResponse from(ChatRoom chatRoom, Long threadCount, Long messageCount, LocalDateTime lastMessageAt) {
        return new AdminListResponse(
                chatRoom.getId(),
                chatRoom.getUser().getId(),
                chatRoom.getUser().getUsername(),
                threadCount,
                messageCount,
                lastMessageAt,
                chatRoom.getCreatedAt()
        );
    }
}