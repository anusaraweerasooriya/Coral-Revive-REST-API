package com.project.rest.scheduling_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentWeatherRequestDTO {
    
    private LocationDTO location;

    @Getter
    @Setter
    public static class LocationDTO {
        private double latitude;
        private double longitude;
    }
}
