package com.project.rest.scheduling_service.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuitableDateDTO implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String date;  
    private WeatherForecastDTO weatherForecast;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeatherForecastDTO implements Serializable {
        private static final long serialVersionUID = 1L; 
        private double temp;
        private double pressure;
        private double humidity;
        private double windSpeed;
        private double rain1h;
        private int cloudsAll;
        private String weatherDescription;
    }
}
