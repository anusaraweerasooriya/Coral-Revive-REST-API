package com.project.rest.scheduling_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponseDTO {
    private WeatherForecastDTO weatherForecast;
    private DivingAnalysisDTO divingAnalysis;
    private String overallSuitability;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeatherForecastDTO {
        private double temp;
        private double visibility;
        private double dewPoint;
        private double pressure;
        private double humidity;
        private double windSpeed;
        private double rain1h;
        private int cloudsAll;
        private String weatherDescription;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DivingAnalysisDTO {
        private String temp;
        private String pressure;
        private String humidity;
        private String windSpeed;
        private String rain1h;
        private String cloudsAll;
    }
}
