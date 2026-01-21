package com.example.lineofduty.domain.dashboard;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.user.UserDetail;
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
public class DashBoardController {
    private final DashboardService dashboardService;

    /**
     * 전체 요약
     * */
    @GetMapping("/summary")
    public ResponseEntity<GlobalResponse> summary(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summary(userDetail)));
    }

    /**
     * 이번 주 입영일정 요약
     * */
    @GetMapping("/enlistment-schedule")
    public ResponseEntity<GlobalResponse> summaryScheduleOfThisWeek(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryScheduleOfThisWeek(userDetail)));
    }

    /**
     * 입영 요청, 연기 요청 요약
     * */
    @GetMapping("/pending")
    public ResponseEntity<GlobalResponse> summaryPending(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryPending(userDetail)));
    }

    /**
     * 연기 요청 사유 요약
     * */
    @GetMapping("/deferments")
    public ResponseEntity<GlobalResponse> summaryDeferments(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, dashboardService.summaryDeferments(userDetail)));
    }

}
