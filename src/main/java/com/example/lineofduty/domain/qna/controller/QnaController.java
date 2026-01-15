package com.example.lineofduty.domain.qna.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.qna.dto.request.QnaResisterRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryListResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaResisterResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaUpdateResponse;
import com.example.lineofduty.domain.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // 질문 등록
    @PostMapping("/{userId}")
    public ResponseEntity<GlobalResponse<QnaResisterResponse>> qnaRegistrationApi(
            @PathVariable Long userId,
            @RequestBody @Valid QnaResisterRequest request
    ) {

        QnaResisterResponse response = qnaService.qnaRegistration(userId,request);

        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.QNA_CREATE_SUCCESS, response));
    }

    //질문 단건 조회
    @GetMapping("/{qnaId}")
    public ResponseEntity<GlobalResponse<QnaInquiryResponse>> qnaInquiryApi(@PathVariable Long qnaId) {

        QnaInquiryResponse response = qnaService.qnaInquiry(qnaId);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_READ_SUCCESS,response));

    }

    //질문 목록 조회
    @GetMapping
    public ResponseEntity<GlobalResponse<QnaInquiryListResponse>> qnaInquiryListApi(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String[] sort
    ) {
        QnaInquiryListResponse response = qnaService.qnaInquiryListResponse(page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_READ_SUCCESS, response));
    }

    //질문 수정
    @PutMapping("/{qnaId}")
    public ResponseEntity<GlobalResponse<QnaUpdateResponse>> qnaUpdateApi(
            @PathVariable Long qnaId,
            @RequestBody @Valid QnaUpdateRequest request
    ) {
        QnaUpdateResponse response = qnaService.qnaUpdate(qnaId,request);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_UPDATE_SUCCESS, response));
    }

    //질문 삭제
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<GlobalResponse<Void>> qnaDeleteApi(@PathVariable Long qnaId) {

        qnaService.qnaDelete(qnaId);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_DELETE_SUCCESS, null));

    }

}
