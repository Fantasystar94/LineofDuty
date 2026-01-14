package com.example.lineofduty.domain.qna.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.qna.dto.request.QnaResisterRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaResisterResponse;
import com.example.lineofduty.domain.qna.service.QnaService;
import com.example.lineofduty.entity.User;
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
    @PostMapping
    public ResponseEntity<GlobalResponse<QnaResisterResponse>> qnaRegistrationApi(
            @RequestBody @Valid QnaResisterRequest request
//            @AuthenticationPrincipal User user
    ) {

        QnaResisterResponse response = qnaService.qnaRegistration(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.QNA_CREATE_SUCCESS, response));
    }

    //질문 단건 조회
    @GetMapping({"/{qnaId}"})
    public ResponseEntity<GlobalResponse<QnaInquiryResponse>> qnaInquiryApi(@PathVariable Long id) {

        QnaInquiryResponse response = qnaService.qnaInquiry(id);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_READ_SUCCESS,response));

    }
}
