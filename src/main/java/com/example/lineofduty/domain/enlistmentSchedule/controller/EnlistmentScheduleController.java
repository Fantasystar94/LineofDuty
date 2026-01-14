package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.request.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.model.response.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.lineofduty.common.model.enums.SuccessMessage.ENLISTMENT_SUCCESS;

@RestController
@RequestMapping("/api/enlistment")
@RequiredArgsConstructor
@Slf4j
public class EnlistmentScheduleController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
    * 입영 가능 일정 조회 - v1 / Authentication 없음
    * */
    @GetMapping
    public ResponseEntity<GlobalResponse<List<EnlistmentScheduleReadResponse>>> getEnlistmentList() {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistmentList(1L)));
    }

    /*
     * 입영 가능 일정 단건 조회 - v1 / Authentication 없음
     * */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GlobalResponse<EnlistmentScheduleReadResponse>> getEnlistment(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(1L, scheduleId)));
    }

    /*
     * 입영 가능 일정 단건 조회 - v1 / Authentication 없음
     * */
    @PostMapping
    public ResponseEntity<GlobalResponse<EnlistmentScheduleReadResponse>> applyEnlistment(@RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.applyEnlistment(1L, request)));
    }


}
