package com.example.lineofduty.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MidWeatherResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        private String dataType;
        private Items items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @JsonPropertyOrder({
            "probabilityOfRain4Am", "probabilityOfRain4Pm", "weatherForecast4Am", "weatherForecast4Pm", "minTemp4", "maxTemp4",
            "probabilityOfRain5Am", "probabilityOfRain5Pm", "weatherForecast5Am", "weatherForecast5Pm", "minTemp5", "maxTemp5",
            "probabilityOfRain6Am", "probabilityOfRain6Pm", "weatherForecast6Am", "weatherForecast6Pm", "minTemp6", "maxTemp6",
            "probabilityOfRain7Am", "probabilityOfRain7Pm", "weatherForecast7Am", "weatherForecast7Pm", "minTemp7", "maxTemp7",
            "probabilityOfRain8", "weatherForecast8", "minTemp8", "maxTemp8",
            "probabilityOfRain9", "weatherForecast9", "minTemp9", "maxTemp9",
            "probabilityOfRain10", "weatherForecast10", "minTemp10", "maxTemp10"
    })
    public static class Item {
        private String regId; // 예보구역코드

        // --- 중기육상예보 (강수확률, 날씨) ---
        // 4일 후
        @JsonAlias("rnSt4Am")
        private int probabilityOfRain4Am;
        @JsonAlias("rnSt4Pm")
        private int probabilityOfRain4Pm;
        @JsonAlias("wf4Am")
        private String weatherForecast4Am;
        @JsonAlias("wf4Pm")
        private String weatherForecast4Pm;

        // 5일 후
        @JsonAlias("rnSt5Am")
        private int probabilityOfRain5Am;
        @JsonAlias("rnSt5Pm")
        private int probabilityOfRain5Pm;
        @JsonAlias("wf5Am")
        private String weatherForecast5Am;
        @JsonAlias("wf5Pm")
        private String weatherForecast5Pm;

        // 6일 후
        @JsonAlias("rnSt6Am")
        private int probabilityOfRain6Am;
        @JsonAlias("rnSt6Pm")
        private int probabilityOfRain6Pm;
        @JsonAlias("wf6Am")
        private String weatherForecast6Am;
        @JsonAlias("wf6Pm")
        private String weatherForecast6Pm;

        // 7일 후
        @JsonAlias("rnSt7Am")
        private int probabilityOfRain7Am;
        @JsonAlias("rnSt7Pm")
        private int probabilityOfRain7Pm;
        @JsonAlias("wf7Am")
        private String weatherForecast7Am;
        @JsonAlias("wf7Pm")
        private String weatherForecast7Pm;

        // 8~10일 후
        @JsonAlias("rnSt8")
        private int probabilityOfRain8;
        @JsonAlias("wf8")
        private String weatherForecast8;
        @JsonAlias("rnSt9")
        private int probabilityOfRain9;
        @JsonAlias("wf9")
        private String weatherForecast9;
        @JsonAlias("rnSt10")
        private int probabilityOfRain10;
        @JsonAlias("wf10")
        private String weatherForecast10;

        // --- 중기기온예보 (최저/최고기온) ---
        // 4일 후
        @JsonAlias("taMin4")
        private int minTemp4;
        @JsonAlias("taMax4")
        private int maxTemp4;

        // 5일 후
        @JsonAlias("taMin5")
        private int minTemp5;
        @JsonAlias("taMax5")
        private int maxTemp5;

        // 6일 후
        @JsonAlias("taMin6")
        private int minTemp6;
        @JsonAlias("taMax6")
        private int maxTemp6;

        // 7일 후
        @JsonAlias("taMin7")
        private int minTemp7;
        @JsonAlias("taMax7")
        private int maxTemp7;

        // 8일 후
        @JsonAlias("taMin8")
        private int minTemp8;
        @JsonAlias("taMax8")
        private int maxTemp8;

        // 9일 후
        @JsonAlias("taMin9")
        private int minTemp9;
        @JsonAlias("taMax9")
        private int maxTemp9;

        // 10일 후
        @JsonAlias("taMin10")
        private int minTemp10;
        @JsonAlias("taMax10")
        private int maxTemp10;

        public void mergeTemperature(Item tempItem) {
            this.minTemp4 = tempItem.minTemp4;
            this.maxTemp4 = tempItem.maxTemp4;
            this.minTemp5 = tempItem.minTemp5;
            this.maxTemp5 = tempItem.maxTemp5;
            this.minTemp6 = tempItem.minTemp6;
            this.maxTemp6 = tempItem.maxTemp6;
            this.minTemp7 = tempItem.minTemp7;
            this.maxTemp7 = tempItem.maxTemp7;
            this.minTemp8 = tempItem.minTemp8;
            this.maxTemp8 = tempItem.maxTemp8;
            this.minTemp9 = tempItem.minTemp9;
            this.maxTemp9 = tempItem.maxTemp9;
            this.minTemp10 = tempItem.minTemp10;
            this.maxTemp10 = tempItem.maxTemp10;
        }
    }
}
