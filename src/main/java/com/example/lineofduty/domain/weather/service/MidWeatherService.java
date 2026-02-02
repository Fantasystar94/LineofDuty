
package com.example.lineofduty.domain.weather.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.weather.dto.MidWeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidWeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    @Value("${weather.api.land-url}")
    private String landApiUrl;

    @Value("${weather.api.temp-url}")
    private String tempApiUrl;

    private final RestClient restClient = RestClient.create();

    public MidWeatherResponse.Item getMidFcst(String landRegId, String tempRegId) {
        String tmFc = calculateTmFc();

        MidWeatherResponse.Item landItem = callApi(landApiUrl, landRegId, tmFc);

        MidWeatherResponse.Item tempItem = callApi(tempApiUrl, tempRegId, tmFc);

        if (landItem != null && tempItem != null) {
            landItem.mergeTemperature(tempItem);
        }

        return landItem;
    }

    private MidWeatherResponse.Item callApi(String url, String regId, String tmFc) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", tmFc)
                .build(true)
                .toUri();

        log.info("Weather API Request: {}", uri);

        try {
            MidWeatherResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(MidWeatherResponse.class);

            if (response == null || response.getResponse() == null || response.getResponse().getBody() == null) {
                throw new CustomException(ErrorMessage.WEATHER_API_ERROR);
            }

            if (!"00".equals(response.getResponse().getHeader().getResultCode())) {
                log.error("Weather API Error: {}", response.getResponse().getHeader().getResultMsg());
                throw new CustomException(ErrorMessage.WEATHER_API_ERROR);
            }

            if (response.getResponse().getBody().getItems().getItem().isEmpty()) {
                return null;
            }

            return response.getResponse().getBody().getItems().getItem().get(0);

        } catch (Exception e) {
            log.error("Failed to call Weather API: {}", url, e);
            throw new CustomException(ErrorMessage.WEATHER_API_ERROR);
        }
    }

    private String calculateTmFc() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        if (hour < 6) {
            return now.minusDays(1).withHour(18).withMinute(0).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        } else if (hour < 18) {
            return now.withHour(6).withMinute(0).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        } else {
            return now.withHour(18).withMinute(0).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        }
    }
}