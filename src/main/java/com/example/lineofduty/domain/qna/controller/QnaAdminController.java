package com.example.lineofduty.domain.qna.controller;


import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerResponse;
import com.example.lineofduty.domain.qna.service.QnaAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
public class QnaAdminController {

    private final QnaAdminService qnaAdminService;


    // 질문 관리자 답변 등록
    @PostMapping("/{qnaId}")
    public ResponseEntity<GlobalResponse<QnaAdminAnswerResponse>> qnaAdminAnswerApi(
            @PathVariable Long qnaId,
            @RequestBody QnaAdminAnswerRequest request) {

        QnaAdminAnswerResponse response = qnaAdminService.qnaAdminAnswer(qnaId,request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.QNA_CREATE_SUCCESS, response));

    }

    // 질문 관리자 답변 수정
    @PutMapping("/{qnaId}")

}
