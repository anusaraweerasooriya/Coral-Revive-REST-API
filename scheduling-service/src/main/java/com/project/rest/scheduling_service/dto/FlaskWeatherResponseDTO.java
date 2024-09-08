package com.project.rest.scheduling_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlaskWeatherResponseDTO {
    private double temp;
    private double pressure;
    private double humidity;
    private double windSpeed;
    private double rain1h;
    private double cloudsAll;
    private String weatherDescription;
}
