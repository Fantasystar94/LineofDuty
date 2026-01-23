package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.lineofduty.common.model.enums.SuccessMessage.ENLISTMENT_APPLY_SUCCESS;

@RestController
@RequestMapping("/api/test/enlistment")
@RequiredArgsConstructor
public class EnlistmentTestController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    @PostMapping
    public ResponseEntity<GlobalResponse> applyEnlistment(@RequestHeader("X-TEST-USER-ID") Long userId, @RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistmentTest(userId, request)));
    }
}
