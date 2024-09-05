package com.project.rest.scheduling_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenWeatherMapResponse {
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private int visibility;
    private List<Weather> weather;

    @Getter
    @Setter
    public static class Main {
        private double temp;
        private double pressure;
        private double humidity;

        @JsonProperty("temp_min")
        private double tempMin;

        @JsonProperty("temp_max")
        private double tempMax;
    }

    @Getter
    @Setter
    public static class Wind {
        private double speed;
    }

    @Getter
    @Setter
    public static class Clouds {
        private int all;
    }

    @Getter
    @Setter
    public static class Rain {
        @JsonProperty("1h")
        private double oneHour;
    }

    @Getter
    @Setter
    public static class Weather {
        private String main;     
        private String description; 
    }
}
