package com.project.rest.scheduling_service.controller;

import com.project.rest.scheduling_service.dto.CurrentWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.SuitableDateDTO;
import com.project.rest.scheduling_service.dto.WeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherResponseDTO;
import com.project.rest.scheduling_service.service.api.WeatherService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduling-service/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @PostMapping("/current-overall-analysis")
    public ResponseEntity<WeatherResponseDTO> getCurrentOverallSuitability(@RequestBody CurrentWeatherRequestDTO requestDTO) {
        WeatherResponseDTO responseDTO = weatherService.analyzeCurrentWeather(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/overall_analysis")
    public ResponseEntity<WeatherResponseDTO> getOverallSuitability(@RequestBody WeatherRequestDTO requestDTO) {
        WeatherResponseDTO responseDTO = weatherService.analyzeWeather(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/suitable-dates")
    public ResponseEntity<List<SuitableDateDTO>> getSuitableDates() {
        List<SuitableDateDTO> suitableDates = weatherService.getSuitableDatesForDiving();
        return ResponseEntity.ok(suitableDates);
    }


    
}
