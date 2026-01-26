package com.example.lineofduty.domain.enlistmentSchedule.controller;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentPatchRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentsPostRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.lineofduty.common.model.enums.SuccessMessage.*;

@RestController
@RequestMapping("/api/enlistment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enlistment API", description = "입영 신청 및 일정 관련 API")
public class EnlistmentScheduleController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
     * 입영 가능 일정 조회
     * */
    @GetMapping
    @Operation(summary = "입영 가능 일정 조회", description = "신청 가능한 입영 일정을 조회합니다.")
    public ResponseEntity<GlobalResponse> getEnlistmentList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistmentList()));
    }

    /*
     * 입영 가능 일정 단건 조회
     * */
    @GetMapping("/{scheduleId}")
    @Operation(summary = "입영 일정 단건 조회", description = "특정 입영 일정을 조회합니다.")
    public ResponseEntity<GlobalResponse> getEnlistment(
            @Parameter(description = "일정 ID", required = true) @PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(scheduleId)));
    }

    /*
     * 입영 신청 - 비관락
     * */
    @PostMapping
    @Operation(summary = "입영 신청", description = "입영을 신청합니다.")
    public ResponseEntity<GlobalResponse> applyEnlistment(@AuthenticationPrincipal UserDetail userDetails, @RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistment(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 목록 조회
     * */
    @GetMapping("/pending")
    @Operation(summary = "입영 신청 목록 조회", description = "대기 중인 입영 신청 목록을 조회합니다.")
    public ResponseEntity<GlobalResponse> getApplicationList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplicationList()));
    }

    /*
     * 입영 신청 단건 조회
     * */
    @GetMapping("/pending/{scheduleId}")
    @Operation(summary = "입영 신청 단건 조회", description = "특정 입영 신청 내역을 조회합니다.")
    public ResponseEntity<GlobalResponse> getApplication(
            @Parameter(description = "일정 ID", required = true) @PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplication(scheduleId)));
    }

    /*
     * 입영 신청 취소
     * */
    @PatchMapping("/{applicationId}/cancel")
    @Operation(summary = "입영 신청 취소", description = "입영 신청을 취소합니다.")
    public ResponseEntity<GlobalResponse> cancelApplication(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "신청 ID", required = true) @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_CANCEL_SUCCESS, enlistmentScheduleService.cancelApplication(userDetails.getUser().getId(), applicationId)));
    }

    /*
     * 입영 신청 승인 / admin 전용
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{applicationId}/approve")
    @Operation(summary = "입영 신청 승인", description = "관리자가 입영 신청을 승인합니다.")
    public ResponseEntity<GlobalResponse> approveApplication(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "신청 ID", required = true) @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPROVE_SUCCESS, enlistmentScheduleService.approveApplication(userDetails.getUser().getId(), applicationId)));
    }

    /*
     * 입영 신청 연기
     * */
    @PostMapping("/deferments")
    @Operation(summary = "입영 연기 신청", description = "입영 연기를 신청합니다.")
    public ResponseEntity<GlobalResponse> defermentsSchedule(@AuthenticationPrincipal UserDetail userDetails, @RequestBody DefermentsPostRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_SUCCESS, enlistmentScheduleService.defermentsSchedule(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 연기 다건조회/어드민
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deferments")
    @Operation(summary = "입영 연기 목록 조회", description = "관리자가 입영 연기 신청 목록을 조회합니다.")
    public ResponseEntity<GlobalResponse> getDefermentList(Pageable pageable) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDefermentList(pageable)));
    }

    /*
     * 입영 신청 연기 단건조회
     * */
    @GetMapping("/deferments/{defermentsId}")
    @Operation(summary = "입영 연기 단건 조회", description = "특정 입영 연기 신청 내역을 조회합니다.")
    public ResponseEntity<GlobalResponse> getDeferment(
            @AuthenticationPrincipal UserDetail userDetails,
            @Parameter(description = "연기 신청 ID", required = true) @PathVariable Long defermentsId) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDeferment(userDetails.getUser().getId(), defermentsId)));
    }

    /*
     * 입영 연기 요청 승인 / 반려
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("admin/deferments/{applicationId}")
    @Operation(summary = "입영 연기 승인/반려", description = "관리자가 입영 연기 요청을 승인하거나 반려합니다.")
    public ResponseEntity<GlobalResponse> processDeferment(
            @Parameter(description = "신청 ID", required = true) @PathVariable Long applicationId,
            @RequestBody DefermentPatchRequest request
    ) {

        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_PROCEED,
                enlistmentScheduleService.processDeferment(
                        applicationId,
                        request.getDecisionStatus())
                )
        );
    }

    /*
     * 입영 연기 요청 일괄 승인 / 반려
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("admin/deferments/bulk")
    @Operation(summary = "입영 연기 일괄 처리", description = "관리자가 입영 연기 요청을 일괄적으로 승인하거나 반려합니다.")
    public ResponseEntity<GlobalResponse> processDefermentBulk(@RequestBody DefermentPatchRequest request) {

        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_PROCEED,
                enlistmentScheduleService.processDefermentBulk(request.getDecisionStatus())
                )
        );
    }

    /*
     * 입영 일정 조회 기능 startDate ~ end Date
     *
     */
    @GetMapping("/search")
    @Operation(summary = "입영 일정 기간 조회", description = "특정 기간 내의 입영 일정을 조회합니다.")
    public ResponseEntity<GlobalResponse> searchEnlistment(
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.searchEnlistment(startDate, endDate, pageable)));
    }
}
