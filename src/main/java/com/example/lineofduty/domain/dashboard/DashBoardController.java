package com.example.lineofduty.domain.dashboard;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.dto.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Dashboard API", description = "관리자용 대시보드 API")
public class DashBoardController {
    private final DashboardService dashboardService;

    /**
     * 전체 요약
     * */
    @GetMapping("/summary")
    @Operation(summary = "전체 요약 조회", description = "대시보드의 전체 요약 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> summary(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summary(userDetail)));
    }

    /**
     * 이번 주 입영일정 요약
     * */
    @GetMapping("/enlistment-schedule")
    @Operation(summary = "금주 입영 일정 요약", description = "이번 주의 입영 일정 요약 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> summaryScheduleOfThisWeek(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryScheduleOfThisWeek(userDetail)));
    }

    /**
     * 입영 요청, 연기 요청 요약
     * */
    @GetMapping("/pending")
    @Operation(summary = "대기 요청 요약", description = "입영 요청 및 연기 요청의 대기 상태 요약 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> summaryPending(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryPending(userDetail)));
    }

    /**
     * 연기 요청 사유 요약
     * */
    @GetMapping("/deferments")
    @Operation(summary = "연기 사유 요약", description = "연기 요청 사유별 요약 정보를 조회합니다.")
    public ResponseEntity<GlobalResponse> summaryDeferments(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryDeferments(userDetail)));
    }

}
