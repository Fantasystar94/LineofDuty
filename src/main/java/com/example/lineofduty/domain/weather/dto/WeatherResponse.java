package com.example.lineofduty.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private String dataType;
        private Items items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String regId; // 예보구역코드

        // --- 중기육상예보 (강수확률, 날씨) ---
        // 4일 후
        private int rnSt4Am;
        private int rnSt4Pm;
        private String wf4Am;
        private String wf4Pm;

        // 5일 후
        private int rnSt5Am;
        private int rnSt5Pm;
        private String wf5Am;
        private String wf5Pm;

        // 6일 후
        private int rnSt6Am;
        private int rnSt6Pm;
        private String wf6Am;
        private String wf6Pm;

        // 7일 후
        private int rnSt7Am;
        private int rnSt7Pm;
        private String wf7Am;
        private String wf7Pm;
        
        // 8~10일 후
        private int rnSt8;
        private String wf8;
        private int rnSt9;
        private String wf9;
        private int rnSt10;
        private String wf10;

        // --- 중기기온예보 (최저/최고기온) ---
        // 4일 후
        private int taMin4;
        private int taMax4;

        // 5일 후
        private int taMin5;
        private int taMax5;

        // 6일 후
        private int taMin6;
        private int taMax6;

        // 7일 후
        private int taMin7;
        private int taMax7;

        // 8일 후
        private int taMin8;
        private int taMax8;

        // 9일 후
        private int taMin9;
        private int taMax9;

        // 10일 후
        private int taMin10;
        private int taMax10;

        // 데이터 병합을 위한 메서드
        public void mergeTemperature(Item tempItem) {
            this.taMin4 = tempItem.taMin4;
            this.taMax4 = tempItem.taMax4;
            this.taMin5 = tempItem.taMin5;
            this.taMax5 = tempItem.taMax5;
            this.taMin6 = tempItem.taMin6;
            this.taMax6 = tempItem.taMax6;
            this.taMin7 = tempItem.taMin7;
            this.taMax7 = tempItem.taMax7;
            this.taMin8 = tempItem.taMin8;
            this.taMax8 = tempItem.taMax8;
            this.taMin9 = tempItem.taMin9;
            this.taMax9 = tempItem.taMax9;
            this.taMin10 = tempItem.taMin10;
            this.taMax10 = tempItem.taMax10;
        }
    }
}
