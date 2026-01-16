package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.deferment.model.request.DefermentsPostRequest;
import com.example.lineofduty.domain.deferment.model.response.DefermentsReadResponse;
import com.example.lineofduty.domain.enlistmentApplication.model.response.EnlistmentApplicationReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.request.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleCreateResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.lineofduty.common.model.enums.SuccessMessage.*;

@RestController
@RequestMapping("/api/enlistment")
@RequiredArgsConstructor
@Slf4j
public class EnlistmentScheduleController {

    private final EnlistmentScheduleService enlistmentScheduleService;
    private static final Long userId = 3L;
    /*
    * 입영 가능 일정 조회 - v1 / Authentication 없음
    * */
    @GetMapping
    public ResponseEntity<GlobalResponse<List<EnlistmentScheduleReadResponse>>> getEnlistmentList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistmentList(userId)));
    }

    /*
     * 입영 가능 일정 단건 조회 - v1 / Authentication 없음
     * */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GlobalResponse<EnlistmentScheduleReadResponse>> getEnlistment(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(userId, scheduleId)));
    }

    /*
     * 입영 신청 - v1 / Authentication 없음
     * */
    @PostMapping
    public ResponseEntity<GlobalResponse<EnlistmentScheduleCreateResponse>> applyEnlistment(@RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistment(userId, request)));
    }

    /*
     * 입영 신청 목록 조회 - v1 / Authentication 없음
     * */
    @GetMapping("/pending")
    public ResponseEntity<GlobalResponse<List<EnlistmentApplicationReadResponse>>> getApplicationList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplicationList()));
    }

    /*
     * 입영 신청 단건 조회 - v1 / Authentication 없음
     * */
    @GetMapping("/pending/{applicationId}")
    public ResponseEntity<GlobalResponse<EnlistmentApplicationReadResponse>> getApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, enlistmentScheduleService.getApplication(applicationId)));
    }

    /*
     * 입영 신청 취소 - v1 / Authentication 없음
     * */
    @PatchMapping("/{applicationId}/cancel")
    public ResponseEntity<GlobalResponse<EnlistmentApplicationReadResponse>> cancelApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_CANCEL_SUCCESS, enlistmentScheduleService.cancelApplication(userId, applicationId)));
    }

    /*
     * 입영 신청 승인 - v1 / Authentication 없음 / admin 전용
     * */
    @PatchMapping("/{applicationId}/approve")
    public ResponseEntity<GlobalResponse<EnlistmentApplicationReadResponse>> approveApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPROVE_SUCCESS, enlistmentScheduleService.approveApplication(userId, applicationId)));
    }

    /*
     * 입영 신청 연기 - v1 / Authentication 없음
     * */
    @PostMapping("/deferments")
    public ResponseEntity<GlobalResponse<?>> defermentsSchedule(@RequestBody DefermentsPostRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_SUCCESS, enlistmentScheduleService.defermentsSchedule(userId, request)));
    }

    /*
     * 입영 신청 연기 다건조회/어드민 - v1 / Authentication 없음
     * */
    @GetMapping("/deferments")
    public ResponseEntity<GlobalResponse<Page<DefermentsReadResponse>>> getDefermentList(Pageable pageable) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDefermentList(userId, pageable)));
    }

    /*
     * 입영 신청 연기 단건조회 - v1 / Authentication 없음
     * */
    @GetMapping("/deferments/{defermentsId}")
    public ResponseEntity<GlobalResponse<DefermentsReadResponse>> getDeferment(@PathVariable Long defermentsId) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDeferment(userId, defermentsId)));
    }

}
