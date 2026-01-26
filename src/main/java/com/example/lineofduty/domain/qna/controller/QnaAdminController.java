package com.example.lineofduty.domain.qna.controller;


import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.qna.service.QnaAdminService;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerUpdateResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
@Tag(name = "QnA Admin API", description = "관리자용 QnA 답변 관련 API")
public class QnaAdminController {

    private final QnaAdminService qnaAdminService;


    // 질문 관리자 답변 등록
    @PostMapping("/{qnaId}")
    @Operation(summary = "QnA 답변 등록", description = "관리자가 질문에 대한 답변을 등록합니다.")
    public ResponseEntity<GlobalResponse> qnaAdminAnswerApi(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody QnaAdminAnswerRequest request) {

        QnaAdminAnswerResponse response = qnaAdminService.qnaAdminAnswer(qnaId,userDetails,request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.QNA_CREATE_SUCCESS, response));
    }

    // 질문 관리자 답변 수정
    @PutMapping("/{qnaId}")
    @Operation(summary = "QnA 답변 수정", description = "관리자가 등록한 답변을 수정합니다.")
    public ResponseEntity<GlobalResponse> qnaAdminAnswerUpdateApi(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetail userDetails,
            @RequestBody QnaAdminAnswerUpdateRequest request) {

        QnaAdminAnswerUpdateResponse response = qnaAdminService.qnaAdminAnswerUpdate(qnaId,userDetails, request);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.QNA_UPDATE_SUCCESS, response));
    }



}
