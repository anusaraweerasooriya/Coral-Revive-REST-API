package com.project.rest.scheduling_service.service.api;

import com.project.rest.scheduling_service.dto.CurrentWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherResponseDTO;

public interface WeatherService {
    WeatherResponseDTO analyzeCurrentWeather(CurrentWeatherRequestDTO requestDTO);
    WeatherResponseDTO analyzeWeather(WeatherRequestDTO requestDTO);
}
