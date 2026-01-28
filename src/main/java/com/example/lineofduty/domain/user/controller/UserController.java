package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.dto.UserResponse;
import com.example.lineofduty.domain.user.dto.UserUpdateRequest;
import com.example.lineofduty.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    // 조회
    @GetMapping("/{userId}")
    public ResponseEntity<GlobalResponse> getMyProfile(@AuthenticationPrincipal UserDetail userDetails) {
        UserResponse response = userService.getMyProfile(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_READ_SUCCESS, response));
    }

    // 수정
    @PutMapping("/{userId}")
    public ResponseEntity<GlobalResponse> updateProfile(
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody UserUpdateRequest requestDto) {
        UserResponse response = userService.updateProfile(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_UPDATE_SUCCESS, response));
    }

    // 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<GlobalResponse> withdrawUser(@AuthenticationPrincipal UserDetail userDetails) {
        userService.withdrawUser(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.successNodata(SuccessMessage.USER_DELETE_SUCCESS));
    }
}