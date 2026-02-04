package com.example.lineofduty.domain.enlistmentSchedule.model;

import com.example.lineofduty.domain.dashboard.model.EnlistmentThisWeekResponse;
import com.example.lineofduty.domain.weather.dto.TodayWeatherResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleOfThisWeekResponse {
    private final TodayWeatherResponse weatherResponse;
    private final EnlistmentThisWeekResponse enlistmentResponse;

}
