package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.UserDetails;
import com.example.lineofduty.domain.user.UserAdminResponse;
import com.example.lineofduty.domain.user.UserWithdrawResponse;
import com.example.lineofduty.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<GlobalResponse> getAllUsers() {
        List<UserAdminResponse> data = userService.getAllUsers();
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_ALL_READ_SUCCESS, data));
    }

    // 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<GlobalResponse> getUser(@PathVariable Long userId) {
        UserAdminResponse data = userService.getUserById(userId);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_READ_ADMIN_SUCCESS, data));
    }

    // 관리자 본인 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<GlobalResponse> withdrawAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        UserWithdrawResponse data = userService.withdrawAdmin(userDetails.getUser().getId());
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_DELETE_ADMIN_SUCCESS, data));
    }
}