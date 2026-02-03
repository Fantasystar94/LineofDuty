package com.example.lineofduty.domain.chatbot.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.chatbot.dto.response.AdminListResponse;
import com.example.lineofduty.domain.chatbot.dto.response.ThreadResponse;
import com.example.lineofduty.domain.chatbot.service.ChatBotAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatBotAdminController {

    private final ChatBotAdminService chatBotAdminService;

    // 채팅 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<GlobalResponse> getChatStatistics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> statistics = chatBotAdminService.getChatStatistics(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, statistics));
    }

    // 전체 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<GlobalResponse> getAllChatRooms(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        PageResponse<AdminListResponse> response = chatBotAdminService.getAllChatRooms(page, size, sort, direction);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.CHATROOM_READ_SUCCESS, response));
    }

    // 특정 유저의 메시지 조회 (스레드 형식)
    @GetMapping("/users/{userId}/messages")
    public ResponseEntity<GlobalResponse> getUserMessages(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        PageResponse<ThreadResponse> response = chatBotAdminService.getUserMessages(userId, page, size, sort, direction);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.CHATROOM_READ_SUCCESS, response));
    }
}
