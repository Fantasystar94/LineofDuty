package com.example.lineofduty.domain.enlistmentSchedule.controller;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
     * 입영 가능 일정 조회
     * */
    @GetMapping
    public ResponseEntity<GlobalResponse> getEnlistmentList(Pageable pageable) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistmentList(pageable)));
    }

    /*
     * 입영 가능 일정 단건 조회
     * */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GlobalResponse> getEnlistment(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(scheduleId)));
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
