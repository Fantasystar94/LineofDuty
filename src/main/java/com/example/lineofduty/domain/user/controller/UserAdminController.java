package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.dto.UserAdminResponse;
import com.example.lineofduty.domain.user.dto.UserWithdrawRequest;
import com.example.lineofduty.domain.user.dto.UserWithdrawResponse;
import com.example.lineofduty.domain.user.service.UserService;
import jakarta.validation.Valid;
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
        List<UserAdminResponse> responseList = userService.getAllUsers();
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_ALL_READ_SUCCESS, responseList));
    }

    // 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<GlobalResponse> getUser(@PathVariable Long userId) {
        UserAdminResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_READ_SUCCESS, response));
    }

    // 관리자 본인 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<GlobalResponse> withdrawAdmin(
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody @Valid UserWithdrawRequest request
            ) {

        UserWithdrawResponse response = userService.withdrawAdmin(userDetails.getUser().getId(), request.getPassword());

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_DELETE_ADMIN_SUCCESS, response));
    }
}