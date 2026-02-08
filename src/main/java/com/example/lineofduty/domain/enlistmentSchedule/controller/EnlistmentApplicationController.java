package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.annotation.LogDescription;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.lineofduty.common.model.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enlistment-applications")
public class EnlistmentApplicationController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
    * 입영 신청
    * */
    @LogDescription("입영 신청")
    @PostMapping
    public ResponseEntity<GlobalResponse> applyEnlistment(@AuthenticationPrincipal UserDetail userDetails, @RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistment(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 목록 조회
     * */
    @LogDescription("입영 신청 목록 조회")
    @GetMapping
    public ResponseEntity<GlobalResponse> getApplicationList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplicationList()));
    }

    /*
     * 입영 신청 단건 조회
     * */
    @LogDescription("입영 신청 단건 조회")
    @GetMapping("/{applicationId}")
    public ResponseEntity<GlobalResponse> getApplication(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplication(userDetails.getUser().getId(), applicationId)));
    }

    /*
     * 입영 신청 취소
     * */
    @LogDescription("입영 신청 취소")
    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<GlobalResponse> cancelApplication(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_CANCEL_SUCCESS, enlistmentScheduleService.cancelApplication(userDetails.getUser().getId(), applicationId)));
    }

}
