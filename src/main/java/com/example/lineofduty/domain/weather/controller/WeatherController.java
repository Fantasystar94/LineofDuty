package com.example.lineofduty.domain.weather.controller;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.domain.weather.dto.WeatherResponse;
import com.example.lineofduty.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/mid-fcst")
    public ResponseEntity<GlobalResponse> getMidFcst(
            @RequestParam(defaultValue = "11B00000") String landRegId, // 육상예보 구역 (서울, 인천, 경기도)
            @RequestParam(defaultValue = "11B10101") String tempRegId  // 기온예보 구역 (서울)
    ) {
        WeatherResponse.Item response = weatherService.getMidFcst(landRegId, tempRegId);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.WEATHER_READ_SUCCESS, response));
    }
}
