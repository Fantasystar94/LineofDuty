package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.dto.UserResponse;
import com.example.lineofduty.domain.user.dto.UserUpdateRequest;
import com.example.lineofduty.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 관련 API")
class UserController {

    private final UserService userService;

    // 조회
    @GetMapping("/{userId}")
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> getMyProfile(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "사용자 ID (Path Variable)", required = true) @PathVariable Long userId) { // 본인 확인용으로 받을 수 있음
        UserResponse data = userService.getMyProfile(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_READ_SUCCESS, data));
    }

    // 수정
    @PutMapping("/{userId}")
    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 프로필 정보를 수정합니다.")
    public ResponseEntity<GlobalResponse> updateProfile(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "사용자 ID (Path Variable)", required = true) @PathVariable Long userId,
            @RequestBody UserUpdateRequest requestDto) {
        UserResponse data = userService.updateProfile(userDetails.getUser().getId(), requestDto);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_UPDATE_SUCCESS, data));
    }

    // 탈퇴
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자를 탈퇴 처리합니다.")
    public ResponseEntity<GlobalResponse> withdrawUser(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "사용자 ID (Path Variable)", required = true) @PathVariable Long userId) {
        userService.withdrawUser(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.successNodata(SuccessMessage.USER_DELETE_SUCCESS));
    }
}