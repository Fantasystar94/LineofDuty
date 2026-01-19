package com.example.lineofduty.domain.notice.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryListResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeUpdateResponse;
import com.example.lineofduty.domain.notice.service.NoticeService;
import com.example.lineofduty.domain.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    // 공지사항 등록
    @PostMapping("/admin/notices")
    public ResponseEntity<GlobalResponse> noticeResisterApi(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody NoticeResisterRequest request
    ) {
        NoticeResisterResponse response = noticeService.noticeResister(userDetails, request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_CREATE_SUCCESS, response));
    }

    //공지사항 상세조회
    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<GlobalResponse> noticeInquiryApi(@PathVariable Long noticeId) {

        NoticeInquiryResponse response = noticeService.noticeInquiry(noticeId);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_READ_SUCCESS, response));
    }

    //공지사항 페이징 조회
    @GetMapping("/notices")
    public ResponseEntity<GlobalResponse> noticeInquiryListApi(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                                                               @RequestParam(value = "sort", defaultValue = "id,desc") String sort) {

        NoticeInquiryListResponse response = noticeService.noticeInquiryList(page, size, sort);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_READ_SUCCESS, response));
    }

    //공지사항 수정
    @PutMapping("/admin/notices/{noticeId}")
    public ResponseEntity<GlobalResponse> noticeUpdateApi(@PathVariable Long noticeId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody NoticeResisterRequest request) {

        NoticeUpdateResponse response = noticeService.noticeUpdate(noticeId,userDetails,request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_UPDATE_SUCCESS, response));
    }

    //공지사항 삭제
    @DeleteMapping("/admin/notices/{noticeId}")
    public ResponseEntity<GlobalResponse> noticeDelete(@PathVariable Long noticeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        noticeService.noticeDelete(noticeId,userDetails);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_DELETE_SUCCESS, null));
    }
}