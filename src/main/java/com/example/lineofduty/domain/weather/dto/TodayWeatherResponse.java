package com.example.lineofduty.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TodayWeatherResponse {
    private String date; // 날짜
    private String time; // 시간
    private String temperature; // 기온 (TMP)
    private String skyStatus; // 하늘상태 (SKY) - 맑음, 구름많음, 흐림
    private String precipitationType; // 강수형태 (PTY) - 없음, 비, 비/눈, 눈, 소나기

    // Raw 데이터(API 응답)를 우리가 원하는 간단한 형태로 변환하는 Factory 메서드
    public static TodayWeatherResponse from(Raw raw) {
        if (raw == null || raw.getResponse() == null || raw.getResponse().getBody() == null ||
            raw.getResponse().getBody().getItems() == null || raw.getResponse().getBody().getItems().getItem() == null ||
            raw.getResponse().getBody().getItems().getItem().isEmpty()) {
            return null;
        }

        List<Item> items = raw.getResponse().getBody().getItems().getItem();
        
        // 가장 빠른 예보 시간 기준 설정 (첫 번째 아이템 기준)
        Item firstItem = items.get(0);
        String date = firstItem.getFcstDate();
        String time = firstItem.getFcstTime();

        String temp = "";
        String sky = "";
        String pty = "";

        for (Item item : items) {
            // 같은 날짜/시간의 데이터만 추출
            if (item.getFcstDate().equals(date) && item.getFcstTime().equals(time)) {
                switch (item.getCategory()) {
                    case "TMP" -> temp = item.getFcstValue();
                    case "SKY" -> sky = convertSkyStatus(item.getFcstValue());
                    case "PTY" -> pty = convertPrecipitationType(item.getFcstValue());
                }
            }
        }
        return new TodayWeatherResponse(date, time, temp, sky, pty);
    }

    private static String convertSkyStatus(String code) {
        return switch (code) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "알 수 없음";
        };
    }

    private static String convertPrecipitationType(String code) {
        return switch (code) {
            case "0" -> "없음";
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            default -> "알 수 없음";
        };
    }

    // --- API 응답 매핑용 내부 클래스 (Raw Data) ---
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Raw {
        private Response response;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String category;
        private String fcstDate;
        private String fcstTime;
        private String fcstValue;
    }
}
