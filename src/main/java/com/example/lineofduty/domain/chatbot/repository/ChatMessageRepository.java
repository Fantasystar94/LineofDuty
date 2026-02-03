package com.example.lineofduty.domain.chatbot.repository;

import com.example.lineofduty.domain.chatbot.ChatMessage;
import com.example.lineofduty.domain.chatbot.ChatRoom;
import com.example.lineofduty.domain.chatbot.dto.ChatRoomStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 채팅방에 속한 모든 메세지를 가져오는 메서드
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    // 채팅방의 모든 USER 메세지(스레드) 조회 (페이징)
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :roomId " +
            "AND cm.messageType = 'USER' " +
            "AND cm.parentMessage IS NULL " +
            "ORDER BY cm.createdAt ASC")
    Page<ChatMessage> findThreadsByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    // 특정 USER 메세지의 AI 답글 조회
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.parentMessage.id = :parentMessageId " +
            "AND cm.messageType = 'AI'")
    Optional<ChatMessage> findReplyByParentId(@Param("parentMessageId") Long parentMessageId);

    // 채팅방의 스레드 수 조회 (USER 메시지 수)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id = :roomId " +
            "AND cm.messageType = 'USER' " +
            "AND cm.parentMessage IS NULL")
    Long countThreadsByRoomId(@Param("roomId") Long roomId);

    // 채팅방의 전체 메시지 수 조회 (USER + AI)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId")
    Long countByChatRoomId(@Param("roomId") Long roomId);

    // 기간별 전체 메시지 수 조회
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.createdAt BETWEEN :startDate AND :endDate")
    Long countMessagesBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 기간별 스레드 수 조회 (USER 메시지만)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.messageType = 'USER' " +
            "AND cm.parentMessage IS NULL " +
            "AND cm.createdAt BETWEEN :startDate AND :endDate")
    Long countThreadsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 기간 내 사용자 메시지(스레드) 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.createdAt BETWEEN :startDate AND :endDate " +
            "AND cm.messageType = 'USER' AND cm.parentMessage IS NULL")
    List<ChatMessage> findUserMessagesBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 기간별 AI 메시지 수 조회
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
            "WHERE cm.messageType = 'AI' " +
            "AND cm.createdAt BETWEEN :startDate AND :endDate")
    Long countAiMessagesBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new ChatRoomStatistics(" +
            "cm.chatRoom.id, " +
            "COUNT(DISTINCT CASE WHEN cm.messageType = com.example.lineofduty.common.model.enums.MessageType.USER AND cm.parentMessage IS NULL THEN cm.id ELSE NULL END), " +
            "COUNT(cm.id), " +
            "MAX(cm.createdAt)) " +
            "FROM ChatMessage cm " +
            "WHERE cm.chatRoom.id IN :roomIds " +
            "GROUP BY cm.chatRoom.id")
    List<ChatRoomStatistics> getStatisticsByRoomIds(@Param("roomIds") List<Long> roomIds);

    // 활성 유저 수 조회 (최근 30일 내 메시지를 보낸 유저)
    @Query("SELECT COUNT(DISTINCT cm.user.id) FROM ChatMessage cm " +
            "WHERE cm.messageType = 'USER' " +
            "AND cm.createdAt >= :sinceDate")
    Long countActiveUsers(@Param("sinceDate") LocalDateTime sinceDate);
}