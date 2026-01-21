package com.example.lineofduty.domain.enlistmentSchedule.controller;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentPatchRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentsPostRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.user.UserDetail;
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
public class EnlistmentScheduleController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
     * 입영 가능 일정 조회 - v1
     * */
    @GetMapping
    public ResponseEntity<GlobalResponse> getEnlistmentList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistmentList()));
    }

    /*
     * 입영 가능 일정 단건 조회 - v1
     * */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GlobalResponse> getEnlistment(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(scheduleId)));
    }

    /*
     * 입영 신청 - v1
     * */
    @PostMapping
    public ResponseEntity<GlobalResponse> applyEnlistment(@AuthenticationPrincipal UserDetail userDetails, @RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistment(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 목록 조회 - v1
     * */
    @GetMapping("/pending")
    public ResponseEntity<GlobalResponse> getApplicationList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplicationList()));
    }

    /*
     * 입영 신청 단건 조회 - v1
     * */
    @GetMapping("/pending/{scheduleId}")
    public ResponseEntity<GlobalResponse> getApplication(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplication(scheduleId)));
    }

    /*
     * 입영 신청 취소 - v1
     * */
    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<GlobalResponse> cancelApplication(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_CANCEL_SUCCESS, enlistmentScheduleService.cancelApplication(userDetails.getUser().getId(), applicationId)));
    }

    /*
     * 입영 신청 승인 - v1 / admin 전용
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{applicationId}/approve")
    public ResponseEntity<GlobalResponse> approveApplication(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPROVE_SUCCESS, enlistmentScheduleService.approveApplication(userDetails.getUser().getId(), applicationId)));
    }

    /*
     * 입영 신청 연기 - v1
     * */
    @PostMapping("/deferments")
    public ResponseEntity<GlobalResponse> defermentsSchedule(@AuthenticationPrincipal UserDetail userDetails, @RequestBody DefermentsPostRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_SUCCESS, enlistmentScheduleService.defermentsSchedule(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 연기 다건조회/어드민 - v1
     * */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/deferments")
    public ResponseEntity<GlobalResponse> getDefermentList(Pageable pageable) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDefermentList(pageable)));
    }

    /*
     * 입영 신청 연기 단건조회 - v1
     * */
    @GetMapping("/deferments/{defermentsId}")
    public ResponseEntity<GlobalResponse> getDeferment(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long defermentsId) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDeferment(userDetails.getUser().getId(), defermentsId)));
    }

    /*
     * 입영 연기 요청 승인 / 반려 - v1
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("admin/deferments/{applicationId}")
    public ResponseEntity<GlobalResponse> processDeferment(
            @PathVariable Long applicationId,
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
     * 입영 연기 요청 일괄 승인 / 반려 - v1
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("admin/deferments/bulk")
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
    public ResponseEntity<GlobalResponse> searchEnlistment(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                           Pageable pageable
    ) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.searchEnlistment(startDate, endDate, pageable)));
    }
}
