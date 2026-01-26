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
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qnas")
@RequiredArgsConstructor
@Tag(name = "QnA API", description = "질문과 답변 관련 API")
public class QnaController {

    private final QnaService qnaService;

    // 질문 등록
    @PostMapping("/{userId}")
    @Operation(summary = "질문 등록", description = "새로운 질문을 등록합니다.")
    public ResponseEntity<GlobalResponse> qnaRegistrationApi(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "사용자 ID (Path Variable)", required = true) @PathVariable Long userId, // 사실상 userDetails에서 가져오므로 PathVariable은 불필요할 수 있으나 기존 코드 유지
            @RequestBody @Valid QnaResisterRequest request) {

        QnaResisterResponse response = qnaService.qnaRegistration(userDetails,request);

        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.success(SuccessMessage.QNA_CREATE_SUCCESS, response));
    }

    //질문 단건 조회
    @GetMapping("/{qnaId}")
    @Operation(summary = "질문 단건 조회", description = "특정 질문을 조회합니다.")
    public ResponseEntity<GlobalResponse> qnaInquiryApi(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long qnaId) {

        QnaInquiryResponse response = qnaService.qnaInquiry(qnaId);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_READ_SUCCESS,response));

    }

    //질문 목록 조회
    @GetMapping
    @Operation(summary = "질문 목록 조회", description = "질문 목록을 페이징하여 조회합니다. 키워드 검색도 가능합니다.")
    public ResponseEntity<GlobalResponse> qnaInquiryListApi(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 (예: id,desc)") @RequestParam(value = "sort", defaultValue = "id,desc") String sort,
            @Parameter(description = "검색 키워드 (제목 또는 내용)") @RequestParam(required = false) String keyword) {

        QnaInquiryListResponse response = qnaService.qnaInquiryListResponse(page, size, sort,keyword);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_READ_SUCCESS, response));
    }

    //질문 수정
    @PutMapping("/{qnaId}")
    @Operation(summary = "질문 수정", description = "등록된 질문을 수정합니다.")
    public ResponseEntity<GlobalResponse> qnaUpdateApi(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "질문 ID", required = true) @PathVariable Long qnaId,
            @RequestBody @Valid QnaUpdateRequest request) {

        QnaUpdateResponse response = qnaService.qnaUpdate(userDetails,qnaId,request);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_UPDATE_SUCCESS, response));
    }

    //질문 삭제
    @DeleteMapping("/{qnaId}")
    @Operation(summary = "질문 삭제", description = "등록된 질문을 삭제합니다.")
    public ResponseEntity<GlobalResponse> qnaDeleteApi(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "질문 ID", required = true) @PathVariable Long qnaId) {

        qnaService.qnaDelete(userDetails,qnaId);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.success(SuccessMessage.QNA_DELETE_SUCCESS, null));
    }

}
