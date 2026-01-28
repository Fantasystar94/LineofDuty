package com.example.lineofduty.domain.enlistmentSchedule.controller;

import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.DefermentsPostRequest;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.user.dto.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.lineofduty.common.model.enums.SuccessMessage.DEFERMENTS_GET_SUCCESS;
import static com.example.lineofduty.common.model.enums.SuccessMessage.DEFERMENTS_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deferments")
public class DefermentController {

    private final EnlistmentScheduleService enlistmentScheduleService;

    /*
     * 입영 신청 연기
     * */
    @PostMapping()
    public ResponseEntity<GlobalResponse> defermentsSchedule(@AuthenticationPrincipal UserDetail userDetails, @RequestBody DefermentsPostRequest request) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_SUCCESS, enlistmentScheduleService.defermentsSchedule(userDetails.getUser().getId(), request)));
    }

    /*
     * 입영 신청 연기 단건조회
     * */
    @GetMapping("/{defermentsId}")
    public ResponseEntity<GlobalResponse> getDeferment(@AuthenticationPrincipal UserDetail userDetails, @PathVariable Long defermentsId) {
        return ResponseEntity.ok(GlobalResponse.success(DEFERMENTS_GET_SUCCESS, enlistmentScheduleService.getDeferment(userDetails.getUser().getId(), defermentsId)));
    }
}
