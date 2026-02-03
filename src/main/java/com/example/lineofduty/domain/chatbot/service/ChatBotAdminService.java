package com.example.lineofduty.domain.chatbot.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.chatbot.ChatMessage;
import com.example.lineofduty.domain.chatbot.ChatRoom;
import com.example.lineofduty.domain.chatbot.dto.ChatRoomStatistics;
import com.example.lineofduty.domain.chatbot.dto.response.AdminListResponse;
import com.example.lineofduty.domain.chatbot.dto.response.ThreadResponse;
import com.example.lineofduty.domain.chatbot.repository.ChatMessageRepository;
import com.example.lineofduty.domain.chatbot.repository.ChatRoomRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotAdminService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 채팅 통계 조회
    @Cacheable(value = "chatStatistics", key = "#startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public Map<String, Object> getChatStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Object> statistics = new HashMap<>();

        // 전체 통계
        Long totalUsers = (long) userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .count();

        Long totalThreads = chatMessageRepository.countThreadsBetweenDates(startDateTime, endDateTime);
        Long totalMessages = chatMessageRepository.countMessagesBetweenDates(startDateTime, endDateTime);
        Long aiMessages = chatMessageRepository.countAiMessagesBetweenDates(startDateTime, endDateTime);
        Long activeUsers = chatMessageRepository.countActiveUsers(endDateTime.minusDays(30));

        statistics.put("totalUsers", totalUsers);
        statistics.put("totalThreads", totalThreads);
        statistics.put("totalMessages", totalMessages);
        statistics.put("totalAiMessages", aiMessages);
        statistics.put("activeUsers", activeUsers);
        statistics.put("averageThreadsPerUser", totalUsers > 0 ? (double) totalThreads / totalUsers : 0);

        // 주요 질문 카테고리 (실제 데이터 기반)
        List<Map<String, Object>> topQuestions = getTopQuestionCategories(startDateTime, endDateTime);
        statistics.put("topQuestions", topQuestions);

        return statistics;
    }

    // 주요 질문 카테고리 분석
    private List<Map<String, Object>> getTopQuestionCategories(LocalDateTime startDate, LocalDateTime endDate) {
        // 사용자 메시지만 조회 (AI 응답 제외)
        List<ChatMessage> userMessages = chatMessageRepository.findUserMessagesBetweenDates(startDate, endDate);

        // 카테고리별 키워드 정의
        Map<String, List<String>> categoryKeywords = new HashMap<>();
        categoryKeywords.put("입영신청", Arrays.asList("입영 신청"));
        categoryKeywords.put("연기신청", Arrays.asList("입영 연기"));
        categoryKeywords.put("입영일정", Arrays.asList("입영 일정", "입영 날짜"));
        categoryKeywords.put("입영준비물", Arrays.asList("준비물", "준비"));

        // 카테고리별 카운트
        Map<String, Long> categoryCounts = new HashMap<>();
        for (String category : categoryKeywords.keySet()) {
            categoryCounts.put(category, 0L);
        }

        // 메시지 분석
        for (ChatMessage message : userMessages) {
            String content = message.getContent().toLowerCase();

            for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();

                // 키워드 매칭
                boolean matched = keywords.stream()
                        .anyMatch(keyword -> content.contains(keyword));

                if (matched) {
                    categoryCounts.put(category, categoryCounts.get(category) + 1);
                }
            }
        }

        // 상위 카테고리 정렬 및 반환 (Top 5)
        return categoryCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> createCategoryMap(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    // 전체 채팅방 목록 조회 (관리자)
    @Transactional(readOnly = true)
    public PageResponse<AdminListResponse> getAllChatRooms(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ChatRoom> chatRooms = chatRoomRepository.findAllChatRooms(pageable);

        if (chatRooms.isEmpty()) {
            return PageResponse.from(Page.empty(pageable));
        }

        // 모든 roomId 수집
        List<Long> roomIds = chatRooms.getContent().stream()
                .map(ChatRoom::getId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 통계 조회
        Map<Long, ChatRoomStatistics> statisticsMap = chatMessageRepository.getStatisticsByRoomIds(roomIds)
                .stream()
                .collect(Collectors.toMap(
                        ChatRoomStatistics::getRoomId,
                        stat -> stat
                ));

        Page<AdminListResponse> responses = chatRooms.map(chatRoom -> {
            ChatRoomStatistics stats = statisticsMap.get(chatRoom.getId());

            if (stats == null) {
                // 메시지가 없는 채팅방
                return AdminListResponse.from(
                        chatRoom,
                        0L,
                        0L,
                        chatRoom.getCreatedAt()
                );
            }

            return AdminListResponse.from(
                    chatRoom,
                    stats.getThreadCount(),
                    stats.getMessageCount(),
                    stats.getLastMessageAt() != null ? stats.getLastMessageAt() : chatRoom.getCreatedAt()
            );
        });

        return PageResponse.from(responses);
    }

    // 특정 유저의 메시지 조회 (관리자) - 스레드 형식
    @Transactional(readOnly = true)
    public PageResponse<ThreadResponse> getUserMessages(Long userId, int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // 유저의 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.CHATROOM_NOT_FOUND));

        // USER 메시지(스레드)만 조회
        Page<ChatMessage> threads = chatMessageRepository.findThreadsByRoomId(
                chatRoom.getId(),
                pageable
        );

        // 각 USER 메시지에 대한 AI 답글 조회
        Page<ThreadResponse> responses = threads.map(userMessage -> {
            ChatMessage aiReply = chatMessageRepository.findReplyByParentId(userMessage.getId())
                    .orElse(null);
            return ThreadResponse.from(userMessage, aiReply);
        });

        return PageResponse.from(responses);
    }

    private Map<String, Object> createCategoryMap(String category, int count) {
        Map<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("count", count);
        return map;
    }
}