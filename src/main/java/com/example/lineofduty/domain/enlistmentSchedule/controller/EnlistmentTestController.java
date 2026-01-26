package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleCreateRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.lineofduty.common.model.enums.SuccessMessage.ENLISTMENT_APPLY_SUCCESS;

@RestController
@RequestMapping("/api/test/enlistment")
@RequiredArgsConstructor
@Tag(name = "Enlistment Test API", description = "입영 신청 테스트용 API")
public class EnlistmentTestController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    @PostMapping
    @Operation(summary = "입영 신청 테스트", description = "테스트용 입영 신청 API입니다. 헤더로 사용자 ID를 받습니다.")
    public ResponseEntity<GlobalResponse> applyEnlistment(
            @Parameter(description = "테스트용 사용자 ID (Header)", required = true) @RequestHeader("X-TEST-USER-ID") Long userId,
            @RequestBody EnlistmentScheduleCreateRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_APPLY_SUCCESS, enlistmentScheduleService.applyEnlistmentTest(userId, request)));
    }
}
