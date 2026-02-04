package com.example.lineofduty.domain.user.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.fileUpload.FileUploadResponse;
import com.example.lineofduty.domain.fileUpload.FileUploadService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.dto.UserResponse;
import com.example.lineofduty.domain.user.dto.UserUpdateRequest;
import com.example.lineofduty.domain.user.dto.UserWithdrawRequest;
import com.example.lineofduty.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;
    private final FileUploadService fileUploadService;

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

    // 프로필 이미지 업로드
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> uploadProfileImage(
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {

        FileUploadResponse fileResponse = fileUploadService.fileUpload(file);

        userService.updateProfileImage(userDetails.getUser().getId(), fileResponse.getUrl());

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.USER_UPDATE_SUCCESS, fileResponse));
    }

    // 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<GlobalResponse> withdrawUser(
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody @Valid UserWithdrawRequest request
            ) {

        userService.withdrawUser(userDetails.getUser().getId(), request.getPassword());

        return ResponseEntity.ok(GlobalResponse.successNodata(SuccessMessage.USER_DELETE_SUCCESS));
    }
}
