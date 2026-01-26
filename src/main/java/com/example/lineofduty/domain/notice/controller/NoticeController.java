package com.example.lineofduty.domain.notice.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryListResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeUpdateResponse;
import com.example.lineofduty.domain.notice.service.NoticeService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Notice API", description = "공지사항 관련 API")
public class NoticeController {

    private final NoticeService noticeService;


    // 공지사항 등록
    @PostMapping("/admin/notices")
    @Operation(summary = "공지사항 등록", description = "관리자가 공지사항을 등록합니다.")
    public ResponseEntity<GlobalResponse> noticeResisterApi(
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody NoticeResisterRequest request
    ) {
        NoticeResisterResponse response = noticeService.noticeResister(userDetails, request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_CREATE_SUCCESS, response));
    }

    //공지사항 상세조회
    @GetMapping("/notices/{noticeId}")
    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항을 조회합니다.")
    public ResponseEntity<GlobalResponse> noticeInquiryApi(
            @Parameter(description = "공지사항 ID", required = true) @PathVariable Long noticeId) {

        NoticeInquiryResponse response = noticeService.noticeInquiry(noticeId);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_READ_SUCCESS, response));
    }

    //공지사항 페이징 조회
    @GetMapping("/notices")
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 페이징하여 조회합니다.")
    public ResponseEntity<GlobalResponse> noticeInquiryListApi(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 (예: id,desc)") @RequestParam(value = "sort", defaultValue = "id,desc") String sort) {

        NoticeInquiryListResponse response = noticeService.noticeInquiryList(page, size, sort);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_READ_SUCCESS, response));
    }

    //공지사항 수정
    @PutMapping("/admin/notices/{noticeId}")
    @Operation(summary = "공지사항 수정", description = "관리자가 공지사항을 수정합니다.")
    public ResponseEntity<GlobalResponse> noticeUpdateApi(
            @Parameter(description = "공지사항 ID", required = true) @PathVariable Long noticeId,
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody NoticeResisterRequest request) {

        NoticeUpdateResponse response = noticeService.noticeUpdate(noticeId,userDetails,request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_UPDATE_SUCCESS, response));
    }

    //공지사항 삭제
    @DeleteMapping("/admin/notices/{noticeId}")
    @Operation(summary = "공지사항 삭제", description = "관리자가 공지사항을 삭제합니다.")
    public ResponseEntity<GlobalResponse> noticeDelete(
            @Parameter(description = "공지사항 ID", required = true) @PathVariable Long noticeId,
            @AuthenticationPrincipal UserDetail userDetails) {

        noticeService.noticeDelete(noticeId,userDetails);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_DELETE_SUCCESS, null));
    }
}