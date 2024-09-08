package com.project.rest.scheduling_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherRequestDTO {
    private String date;
    private String location;
}
