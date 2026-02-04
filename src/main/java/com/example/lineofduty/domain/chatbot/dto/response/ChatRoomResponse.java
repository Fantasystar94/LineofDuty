package com.example.lineofduty.domain.chatbot.dto.response;

import com.example.lineofduty.domain.chatbot.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private Long userId;
    private Long messageCount;        // 전체 메시지 수 (USER + AI)
    private Long threadCount;         // 스레드 수 (USER 메시지만)
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ChatRoomResponse from(ChatRoom chatRoom, Long messageCount, Long threadCount) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getUser().getId(),
                messageCount,
                threadCount,
                chatRoom.getCreatedAt(),
                chatRoom.getModifiedAt()
        );
    }
}