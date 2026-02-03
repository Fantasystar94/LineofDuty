package com.example.lineofduty.domain.chatbot.dto.response;

import com.example.lineofduty.domain.chatbot.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ReplyResponse {
    private Long messageId;
    private Long userId;
    private String username;
    private String content;
    private String messageType;
    private Long parentMessageId;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;

    public static ReplyResponse from(ChatMessage aiMessage) {
        return new ReplyResponse(
                aiMessage.getId(),
                null,
                "AI Assistant",
                aiMessage.getContent(),
                aiMessage.getMessageType().name(),
                aiMessage.getParentMessage() != null ? aiMessage.getParentMessage().getId() : null,
                aiMessage.getMetadata(),
                aiMessage.getCreatedAt()
        );
    }
}