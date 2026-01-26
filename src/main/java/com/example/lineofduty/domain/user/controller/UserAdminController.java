package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.dto.UserAdminResponse;
import com.example.lineofduty.domain.user.dto.UserWithdrawResponse;
import com.example.lineofduty.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Admin API", description = "관리자용 사용자 관리 API")
public class UserAdminController {

    private final UserService userService;

    // 전체 조회
    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "관리자가 모든 사용자를 조회합니다.")
    public ResponseEntity<GlobalResponse> getAllUsers() {
        List<UserAdminResponse> data = userService.getAllUsers();
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_ALL_READ_SUCCESS, data));
    }

    // 상세 조회
    @GetMapping("/{userId}")
    @Operation(summary = "사용자 상세 조회", description = "관리자가 특정 사용자의 상세 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> getUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        UserAdminResponse data = userService.getUserById(userId);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_READ_ADMIN_SUCCESS, data));
    }

    // 관리자 본인 탈퇴
    @DeleteMapping("/{userId}")
    @Operation(summary = "관리자 탈퇴", description = "관리자가 본인을 탈퇴 처리합니다.")
    public ResponseEntity<GlobalResponse> withdrawAdmin(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "관리자 ID", required = true) @PathVariable Long userId) {
        UserWithdrawResponse data = userService.withdrawAdmin(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_DELETE_ADMIN_SUCCESS, data));
    }
}