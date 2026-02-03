package com.example.lineofduty.domain.chatbot;

import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms", uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_room", columnNames = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 채팅방 생성 (정적 팩토리 메서드)
    public static ChatRoom from(User user) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.user = user;
        return chatRoom;
    }
}