package com.project.rest.scheduling_service.service.api;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import com.project.rest.scheduling_service.dto.CurrentWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.SuitableDateDTO;
import com.project.rest.scheduling_service.dto.WeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherResponseDTO;

public interface WeatherService {
    WeatherResponseDTO analyzeCurrentWeather(CurrentWeatherRequestDTO requestDTO);
    WeatherResponseDTO analyzeWeather(WeatherRequestDTO requestDTO);

    //WeatherService(RedisTemplate<String, Object> redisTemplate);
    List<SuitableDateDTO> getSuitableDatesForDiving();
}
