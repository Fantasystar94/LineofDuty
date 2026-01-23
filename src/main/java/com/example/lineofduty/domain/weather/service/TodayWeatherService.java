package com.example.lineofduty.domain.weather.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.weather.dto.TodayWeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodayWeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    @Value("${weather.api.short-term-url}")
    private String apiUrl;

    private final RestClient restClient = RestClient.create();

    @Transactional(readOnly = true)
    public TodayWeatherResponse getTodayWeather(int nx, int ny) {
        // 1. 현재 시간 기준으로 base_date, base_time 계산
        String[] baseDateTime = calculateBaseDateTime();
        String baseDate = baseDateTime[0];
        String baseTime = baseDateTime[1];

        // 2. API 호출
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        log.info("Short Term Weather API Request: {}", uri);

        try {
            // 1. API 응답을 Raw 데이터 구조(Inner Class)로 매핑
            TodayWeatherResponse.Raw rawResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(TodayWeatherResponse.Raw.class);

            // 2. Raw 데이터를 우리가 원하는 TodayWeatherResponse 객체로 변환 (파싱 로직 위임)
            TodayWeatherResponse response = TodayWeatherResponse.from(rawResponse);

            if (response == null) {
                throw new CustomException(ErrorMessage.WEATHER_API_ERROR);
            }

            return response;

        } catch (Exception e) {
            log.error("Failed to call Short Term Weather API", e);
            throw new CustomException(ErrorMessage.WEATHER_API_ERROR);
        }
    }

    private String[] calculateBaseDateTime() {
        LocalDateTime now = LocalDateTime.now();
        
        // 단기예보(VilageFcst)는 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (1일 8회) 발표
        // API 제공 시간은 발표 시간 + 10분 뒤 (예: 02:10)
        
        // 현재 시간에서 10분을 뺌 (API 제공 시간 고려)
        LocalDateTime targetTime = now.minusMinutes(10);
        
        int hour = targetTime.getHour();
        String baseTime;
        
        if (hour < 2) {
            // 02시 이전이면 전날 23시 예보 사용
            targetTime = targetTime.minusDays(1);
            baseTime = "2300";
        } else if (hour < 5) {
            baseTime = "0200";
        } else if (hour < 8) {
            baseTime = "0500";
        } else if (hour < 11) {
            baseTime = "0800";
        } else if (hour < 14) {
            baseTime = "1100";
        } else if (hour < 17) {
            baseTime = "1400";
        } else if (hour < 20) {
            baseTime = "1700";
        } else if (hour < 23) {
            baseTime = "2000";
        } else {
            baseTime = "2300";
        }

        String baseDate = targetTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        return new String[]{baseDate, baseTime};
    }
}
