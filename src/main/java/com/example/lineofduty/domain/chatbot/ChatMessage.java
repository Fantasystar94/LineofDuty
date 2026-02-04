package com.example.lineofduty.domain.chatbot;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.MessageType;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_room_created", columnList = "room_id, created_at"),
        @Index(name = "idx_parent_message", columnList = "parent_message_id"),
        @Index(name = "idx_message_type", columnList = "message_type"),
        @Index(name = "idx_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_room_type_parent", columnList = "room_id, message_type, parent_message_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private ChatMessage parentMessage;

    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> children = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> metadata;

    // USER 메시지 생성 (정적 팩토리 메서드)
    public static ChatMessage from(ChatRoom chatRoom, User user, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.user = user;
        message.parentMessage = null;
        message.content = content;
        message.messageType = MessageType.USER;
        message.metadata = null;
        return message;
    }

    // AI 답글 생성 (정적 팩토리 메서드)
    public static ChatMessage from(ChatRoom chatRoom, ChatMessage parentMessage, String content, Map<String, Object> metadata) {
        if (parentMessage.getMessageType() != MessageType.USER) {
            throw new CustomException(ErrorMessage.AI_ONLY_COMMENT_USER_MESSAGE);
        }

        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.user = null;
        message.parentMessage = parentMessage;
        message.content = content;
        message.messageType = MessageType.AI;
        message.metadata = metadata;
        return message;
    }

    // 이 메시지가 최상위 메시지인지 확인
    public boolean isTopLevel() {
        return this.parentMessage == null;
    }


    // 이 메시지가 답글을 가질 수 있는지 확인
    public boolean canHaveReply() {
        return this.messageType == MessageType.USER;
    }
}