package com.project.rest.scheduling_service.service.impl;

import com.project.rest.scheduling_service.dto.CurrentWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherResponseDTO;
import com.project.rest.scheduling_service.dto.FlaskWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.FlaskWeatherResponseDTO;
import com.project.rest.scheduling_service.dto.OpenWeatherMapResponse;
import com.project.rest.scheduling_service.dto.WeatherRequestDTO;
import com.project.rest.scheduling_service.service.api.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    @Override
    public WeatherResponseDTO analyzeCurrentWeather(CurrentWeatherRequestDTO requestDTO) {
        // Fetch weather details from an external API
        WeatherResponseDTO.WeatherForecastDTO weatherData = fetchWeatherData(requestDTO.getLocation().getLatitude(), requestDTO.getLocation().getLongitude());

        // Analyze the weather conditions and calculate the diving suitability
        WeatherResponseDTO.DivingAnalysisDTO divingAnalysis = analyzeDivingConditions(weatherData);

        // Determine overall suitability
        String overallSuitability = classifyDivingConditions(divingAnalysis);

        // Return the complete response
        WeatherResponseDTO responseDTO = new WeatherResponseDTO();
        responseDTO.setWeatherForecast(weatherData);
        responseDTO.setDivingAnalysis(divingAnalysis);
        responseDTO.setOverallSuitability(overallSuitability);

        return responseDTO;
    }

    @Override
    public WeatherResponseDTO analyzeWeather(WeatherRequestDTO requestDTO) {
        // Fetch weather details from an external API
        WeatherResponseDTO.WeatherForecastDTO weatherData = fetchWeatherDataFromFlask(requestDTO.getDate());

        // Analyze the weather conditions and calculate the diving suitability
        WeatherResponseDTO.DivingAnalysisDTO divingAnalysis = analyzeDivingConditions(weatherData);

        // Determine overall suitability
        String overallSuitability = classifyDivingConditions(divingAnalysis);

        // Return the complete response
        WeatherResponseDTO responseDTO = new WeatherResponseDTO();
        responseDTO.setWeatherForecast(weatherData);
        responseDTO.setDivingAnalysis(divingAnalysis);
        responseDTO.setOverallSuitability(overallSuitability);

        return responseDTO;
    }

    private WeatherResponseDTO.WeatherForecastDTO fetchWeatherData(double latitude, double longitude) {
        // Construct the API request URL using latitude and longitude
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")  // For temperature in Celsius
                .build()
                .toUri();

        // Fetch the data from the API
        RestTemplate restTemplate = new RestTemplate();
        OpenWeatherMapResponse apiResponse = restTemplate.getForObject(uri, OpenWeatherMapResponse.class);

        // Map the API response to your WeatherForecastDTO
        WeatherResponseDTO.WeatherForecastDTO weatherData = new WeatherResponseDTO.WeatherForecastDTO();
        weatherData.setTemp(apiResponse.getMain().getTemp());
        weatherData.setVisibility(apiResponse.getVisibility() / 1000.0); // Convert visibility to kilometers
        weatherData.setDewPoint(apiResponse.getMain().getTemp() - ((100 - apiResponse.getMain().getHumidity()) / 5)); // Approximation of dew point
        weatherData.setPressure(apiResponse.getMain().getPressure());
        weatherData.setHumidity(apiResponse.getMain().getHumidity());
        weatherData.setWindSpeed(apiResponse.getWind().getSpeed());
        weatherData.setRain1h(apiResponse.getRain() != null ? apiResponse.getRain().getOneHour() : 0.0);
        weatherData.setCloudsAll(apiResponse.getClouds().getAll());

        // Extract weather description
        if (apiResponse.getWeather() != null && !apiResponse.getWeather().isEmpty()) {
            weatherData.setWeatherDescription(apiResponse.getWeather().get(0).getMain()); // e.g., "Cloudy", "Sunny"
        } else {
            weatherData.setWeatherDescription("Unknown");
        }

        return weatherData;
    }

    private WeatherResponseDTO.WeatherForecastDTO fetchWeatherDataFromFlask(String targetDate) {
        // Construct the request to the Flask server
        FlaskWeatherRequestDTO flaskRequest = new FlaskWeatherRequestDTO();
        flaskRequest.setTarget_date(targetDate);

        // Send request to Flask server
        RestTemplate restTemplate = new RestTemplate();
        FlaskWeatherResponseDTO flaskResponse = restTemplate.postForObject(flaskApiUrl + "/api/weather/forecast_weather", flaskRequest, FlaskWeatherResponseDTO.class);

        // Map the Flask response to your WeatherForecastDTO
        WeatherResponseDTO.WeatherForecastDTO weatherData = new WeatherResponseDTO.WeatherForecastDTO();
        weatherData.setTemp(flaskResponse.getTemp());
        weatherData.setPressure(flaskResponse.getPressure());
        weatherData.setHumidity(flaskResponse.getHumidity());
        weatherData.setWindSpeed(flaskResponse.getWindSpeed());
        weatherData.setRain1h(flaskResponse.getRain1h());
        weatherData.setCloudsAll((int) flaskResponse.getCloudsAll());
        weatherData.setWeatherDescription(flaskResponse.getWeatherDescription());

        return weatherData;
    }

    private WeatherResponseDTO.DivingAnalysisDTO analyzeDivingConditions(WeatherResponseDTO.WeatherForecastDTO weatherData) {
        WeatherResponseDTO.DivingAnalysisDTO analysis = new WeatherResponseDTO.DivingAnalysisDTO();

        // Use provided analysis logic
        analysis.setTemp(weatherData.getTemp() >= 24 && weatherData.getTemp() <= 30 ? "Suitable" : "Not suitable");
        analysis.setPressure(weatherData.getPressure() >= 1000 && weatherData.getPressure() <= 1020 ? "Steady" : "Fluctuating");
        analysis.setHumidity(weatherData.getHumidity() >= 40 && weatherData.getHumidity() <= 70 ? "Comfortable" : (weatherData.getHumidity() > 70 ? "High" : "Low"));
        analysis.setWindSpeed(weatherData.getWindSpeed() <= 10 ? "Suitable" : "High");
        analysis.setRain1h(weatherData.getRain1h() < 1 ? "Minimal" : "Heavy");
        analysis.setCloudsAll(weatherData.getCloudsAll() < 60 ? "Moderate" : "High");

        return analysis;
    }

    private String classifyDivingConditions(WeatherResponseDTO.DivingAnalysisDTO analysis) {
        return analysis.getTemp().equals("Suitable") &&
               analysis.getPressure().equals("Steady") &&
               analysis.getHumidity().equals("Comfortable") &&
               analysis.getWindSpeed().equals("Suitable") &&
               analysis.getRain1h().equals("Minimal") &&
               analysis.getCloudsAll().equals("Moderate") ?
               "Suitable for diving." : "Not suitable for diving.";
    }

    
}
