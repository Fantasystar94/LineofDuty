package com.example.lineofduty.domain.enlistmentSchedule.controller;
import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.dashboard.model.EnlistmentThisWeekResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.EnlistmentScheduleReadResponse;
import com.example.lineofduty.domain.enlistmentSchedule.model.ScheduleOfThisWeekResponse;
import com.example.lineofduty.domain.enlistmentSchedule.service.EnlistmentScheduleService;
import com.example.lineofduty.domain.weather.dto.TodayWeatherResponse;
import com.example.lineofduty.domain.weather.service.TodayWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.example.lineofduty.common.model.enums.SuccessMessage.*;

@RestController
@RequestMapping("/api/enlistment")
@RequiredArgsConstructor
@Slf4j
public class EnlistmentScheduleController {

    private final EnlistmentScheduleService enlistmentScheduleService;
    private final TodayWeatherService shortTermWeatherService;

    /*
     * 입영 가능 일정 조회
     * */
    @GetMapping
    public ResponseEntity<GlobalResponse> getEnlistmentList(Pageable pageable) {

        List<EnlistmentScheduleReadResponse> list = enlistmentScheduleService.getEnlistmentList(pageable);

        Page<EnlistmentScheduleReadResponse> data = new PageImpl<>(list,pageable, list.size());

        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, data));
    }

    /*
     * 입영 가능 일정 단건 조회
     * */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GlobalResponse> getEnlistment(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_SUCCESS, enlistmentScheduleService.getEnlistment(scheduleId)));
    }

    /**
     * 이번 주 입영일정 요약
     * */
    @GetMapping("/thisWeek")
    public ResponseEntity<GlobalResponse> summaryScheduleOfThisWeek(
            @RequestParam(defaultValue = "36") int nx, // 논산 훈련소 기준 X 좌표
            @RequestParam(defaultValue = "127") int ny // 논산 훈련소 기준 Y 좌표
    ) {

        TodayWeatherResponse weatherResponse = shortTermWeatherService.getTodayWeather(nx, ny);

        EnlistmentThisWeekResponse enlistmentResponse = enlistmentScheduleService.summaryScheduleOfThisWeek();

        ScheduleOfThisWeekResponse data = new ScheduleOfThisWeekResponse(weatherResponse, enlistmentResponse);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.SUMMARY_SUCCESS, data));
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

        List<EnlistmentScheduleReadResponse> list = enlistmentScheduleService.searchEnlistment(startDate, endDate, pageable);

        Page<EnlistmentScheduleReadResponse> data = new PageImpl<>(list,pageable, list.size());

        return ResponseEntity.ok(GlobalResponse.success(ENLISTMENT_LIST_SUCCESS, data));
    }
}
