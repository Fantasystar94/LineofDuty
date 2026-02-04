package com.example.lineofduty.domain.chatbot.dto.response;

import com.example.lineofduty.domain.chatbot.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ThreadResponse {
    private Long messageId;
    private Long userId;
    private String username;
    private String content;
    private String messageType;
    private Long parentMessageId;
    private Boolean hasReply;
    private ReplyResponse reply;
    private LocalDateTime createdAt;

    public static ThreadResponse from(ChatMessage userMessage, ChatMessage aiReply) {
        return new ThreadResponse(
                userMessage.getId(),
                userMessage.getUser().getId(),
                userMessage.getUser().getUsername(),
                userMessage.getContent(),
                userMessage.getMessageType().name(),
                null,
                aiReply != null,
                aiReply != null ? ReplyResponse.from(aiReply) : null,
                userMessage.getCreatedAt()
        );
    }
}