package com.example.lineofduty.domain.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomStatistics {
    private Long roomId;
    private Long threadCount;
    private Long messageCount;
    private LocalDateTime lastMessageAt;
}
