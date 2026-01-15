package com.example.lineofduty.domain.notice.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    // 공지사항 등록
    @PostMapping("/admin/notices/{userId}")
    public ResponseEntity<GlobalResponse<NoticeResisterResponse>> noticeResisterApi(
            @PathVariable Long userId,
            @RequestBody NoticeResisterRequest request
            ) {
        NoticeResisterResponse response = noticeService.noticeResister(userId,request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_CREATE_SUCCESS,response));
    }

    //공지사항 상세조회
    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<GlobalResponse<NoticeInquiryResponse>> noticeInquiryApi(@PathVariable Long noticeId) {

        NoticeInquiryResponse response = noticeService.noticeInquiry(noticeId);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.NOTICE_READ_SUCCESS,response));


    }
}
