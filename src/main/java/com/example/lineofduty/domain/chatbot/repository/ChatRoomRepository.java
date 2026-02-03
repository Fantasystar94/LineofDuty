package com.example.lineofduty.domain.chatbot.repository;

import com.example.lineofduty.domain.chatbot.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 유저 ID로 채팅방 조회 (1인 1채팅방)
    Optional<ChatRoom> findByUserId(Long userId);

    // 유저 ID로 채팅방 존재 여부 확인
    boolean existsByUserId(Long userId);

    // 전체 채팅방 수 조회
    @Query("SELECT Count(cr) FROM ChatRoom cr")
    Long countAllChatRooms();

    // 전체 채팅방 목록 조회 (관리자용)
    @Query("SELECT cr FROM ChatRoom cr ORDER BY cr.createdAt DESC")
    Page<ChatRoom> findAllChatRooms(Pageable pageable);
}
