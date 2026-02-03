package com.example.lineofduty.domain.chatbot.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import com.example.lineofduty.domain.chatbot.dto.request.SendRequest;
import com.example.lineofduty.domain.chatbot.dto.response.ChatRoomResponse;
import com.example.lineofduty.domain.chatbot.dto.response.ThreadResponse;
import com.example.lineofduty.domain.chatbot.service.ChatBotService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;

    // 내 채팅방 조회 (없으면 자동 생성)
    @GetMapping("/room")
    public ResponseEntity<GlobalResponse> getMyChatRoom(@AuthenticationPrincipal UserDetail userDetail) {
        ChatRoomResponse response = chatBotService.getMyChatRoom(userDetail.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.CHATROOM_READ_SUCCESS, response));
    }

    // 채팅방 초기화 (대화 내역 삭제)
    @DeleteMapping("/room/reset")
    public ResponseEntity<GlobalResponse> resetChatRoom(@AuthenticationPrincipal UserDetail userDetail) {
        chatBotService.resetChatRoom(userDetail.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.successNodata(SuccessMessage.CHATROOM_DELETE_SUCCESS));
    }

    // 메시지 전송 (일반) - 스레드 형식 응답
    @PostMapping("/messages")
    public ResponseEntity<GlobalResponse> sendMessage(@AuthenticationPrincipal UserDetail userDetail, @Valid @RequestBody SendRequest request) {
        ThreadResponse response = chatBotService.sendMessage(userDetail.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.success(SuccessMessage.CHAT_MESSAGE_CREATE_SUCCESS, response));
    }

    // 메시지 목록 조회 (스레드 형식)
    @GetMapping("/messages")
    public ResponseEntity<GlobalResponse> getMessages(@AuthenticationPrincipal UserDetail userDetail, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        PageResponse<ThreadResponse> response = chatBotService.getMessages(userDetail.getUser().getId(), page, size, sort, direction);
        return ResponseEntity.status(HttpStatus.OK).
                body(GlobalResponse.success(SuccessMessage.CHATROOM_READ_SUCCESS, response));
    }

    // 특정 스레드 조회
    @GetMapping("/messages/{messageId}/thread")
    public ResponseEntity<GlobalResponse> getThread(@AuthenticationPrincipal UserDetail userDetail, @PathVariable Long messageId) {
        ThreadResponse response = chatBotService.getThread(userDetail.getUser().getId(), messageId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponse.success(SuccessMessage.CHATROOM_READ_SUCCESS, response));
    }
}
