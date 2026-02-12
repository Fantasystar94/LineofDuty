package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentPatchRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.lineofduty.common.model.enums.SuccessMessage.*;
import static com.example.lineofduty.common.model.enums.SuccessMessage.DEFERMENTS_PROCEED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class EnlistmentAdminController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
     * 입영 신청 승인 / admin 전용
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("enlistment-applications/{applicationId}/approve")
    public ResponseEntity<GlobalResponse> approveApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPROVE_SUCCESS, enlistmentScheduleService.approveApplication(applicationId)));
    }

    /*
     * 입영 신청 일괄 승인 / admin 전용
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("enlistment-applications/approve/bulk")
    public ResponseEntity<GlobalResponse> approveApplication() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPROVE_SUCCESS, enlistmentScheduleService.approveApplicationBulk()));
    }

    /*
     * 입영 신청 연기 다건조회/어드민
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deferments")
    public ResponseEntity<GlobalResponse> getDefermentList(Pageable pageable) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDefermentList(pageable)));
    }

    /*
     * 입영 연기 요청 승인 / 반려
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deferments/{defermentsId}")
    public ResponseEntity<GlobalResponse> processDeferment(
            @PathVariable Long defermentsId,
            @RequestBody DefermentPatchRequest request
    ) {

        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_PROCEED,
                enlistmentScheduleService.processDeferment(
                        defermentsId,
                        request
                )
        ));
    }

    /*
     * 입영 연기 요청 일괄 승인 / 반려
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deferments/bulk")
    public ResponseEntity<GlobalResponse> processDefermentBulk(@RequestBody DefermentPatchRequest request) {

        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_PROCEED,
                        enlistmentScheduleService.processDefermentBulk(request.getDecisionStatus())
                )
        );
    }

    /*
     * 입영 일정 생성
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/enlistment-schedule")
    public ResponseEntity<GlobalResponse> createEnlistmentSchedule() {
        List<EnlistmentScheduleReadResponse> data = enlistmentScheduleService.createEnlistmentYear();
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, data));
    }

}
