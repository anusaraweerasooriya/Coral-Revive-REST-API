package com.project.rest.scheduling_service.service.impl;

import com.project.rest.scheduling_service.dto.CurrentWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.WeatherResponseDTO;
import com.project.rest.scheduling_service.dto.FlaskWeatherRequestDTO;
import com.project.rest.scheduling_service.dto.FlaskWeatherResponseDTO;
import com.project.rest.scheduling_service.dto.OpenWeatherMapResponse;
import com.project.rest.scheduling_service.dto.SuitableDateDTO;
import com.project.rest.scheduling_service.dto.WeatherRequestDTO;
import com.project.rest.scheduling_service.service.api.WeatherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    private RedisTemplate<String, Object> redisTemplate;
    private static final String SUITABLE_DATES_KEY = "suitableDates";

    public WeatherServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<SuitableDateDTO> getSuitableDatesForDiving() {
        List<SuitableDateDTO> suitableDates = (List<SuitableDateDTO>) redisTemplate.opsForValue().get(SUITABLE_DATES_KEY);
        
        // Check if the data is available in Redis
        if (suitableDates != null) {
            // Check if the current date + 10 days is within the 30-day data
            LocalDate currentDate = LocalDate.now();
            LocalDate tenthDay = currentDate.plusDays(10);

            // If the 10th date is within the stored 30 days, return the cached data
            if (!suitableDates.isEmpty() && suitableDates.get(suitableDates.size() - 1).getDate().compareTo(tenthDay.toString()) >= 0) {
                return suitableDates;
            }

            // If not valid, clear the existing cache
            redisTemplate.delete(SUITABLE_DATES_KEY);
        }

        // Generate new data if Redis cache is empty or outdated
        suitableDates = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();
            WeatherRequestDTO requestDTO = new WeatherRequestDTO();
            requestDTO.setDate(dateStr);
            WeatherResponseDTO weatherResponse = analyzeWeather(requestDTO);

            if (weatherResponse.getOverallSuitability().equals("Suitable for diving.")) {
                SuitableDateDTO suitableDate = new SuitableDateDTO();
                suitableDate.setDate(dateStr);
                WeatherResponseDTO.WeatherForecastDTO weatherForecast = weatherResponse.getWeatherForecast();
                SuitableDateDTO.WeatherForecastDTO suitableWeatherForecast = new SuitableDateDTO.WeatherForecastDTO(
                    weatherForecast.getTemp(),
                    weatherForecast.getPressure(),
                    weatherForecast.getHumidity(),
                    weatherForecast.getWindSpeed(),
                    weatherForecast.getRain1h(),
                    weatherForecast.getCloudsAll(),
                    weatherForecast.getWeatherDescription()
                );
                suitableDate.setWeatherForecast(suitableWeatherForecast);
                suitableDates.add(suitableDate);
            }
        }

        // Store the new list in Redis
        redisTemplate.opsForValue().set(SUITABLE_DATES_KEY, suitableDates);

        return suitableDates;
    }

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
        weatherData.setDewPoint(apiResponse.getMain().getTemp() - ((100 - apiResponse.getMain().getHumidity()) / 5));
        weatherData.setPressure(apiResponse.getMain().getPressure());
        weatherData.setHumidity(apiResponse.getMain().getHumidity());
        weatherData.setWindSpeed(apiResponse.getWind().getSpeed());
        weatherData.setRain1h(apiResponse.getRain() != null ? apiResponse.getRain().getOneHour() : 0.0);
        weatherData.setCloudsAll(apiResponse.getClouds().getAll());

        // Extract weather description
        if (apiResponse.getWeather() != null && !apiResponse.getWeather().isEmpty()) {
            weatherData.setWeatherDescription(apiResponse.getWeather().get(0).getMain());
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
    
        analysis.setTemp(weatherData.getTemp() >= 24 && weatherData.getTemp() <= 27 ? "Suitable" : "Not suitable");
        analysis.setPressure(weatherData.getPressure() >= 1010 && weatherData.getPressure() <= 1020 ? "Steady" : "Fluctuating");
        analysis.setHumidity(weatherData.getHumidity() >= 50 && weatherData.getHumidity() <= 80 ? "Comfortable" : (weatherData.getHumidity() > 80 ? "High" : "Low"));
        analysis.setWindSpeed(weatherData.getWindSpeed() <= 20 ? "Suitable" : "High");
        analysis.setRain1h(weatherData.getRain1h() < 1 ? "Minimal" : "Heavy");
        analysis.setCloudsAll(weatherData.getCloudsAll() < 60 ? "Moderate" : "High");
        
        return analysis;
    }
    
    private String classifyDivingConditions(WeatherResponseDTO.DivingAnalysisDTO analysis) {
        // Modify classification to allow some tolerance for less-than-ideal conditions
        int suitableCount = 0;
    
        // Count the number of suitable conditions
        if (analysis.getTemp().equals("Suitable")) suitableCount++;
        if (analysis.getPressure().equals("Steady")) suitableCount++;
        if (analysis.getHumidity().equals("Comfortable")) suitableCount++;
        if (analysis.getWindSpeed().equals("Suitable") || (analysis.getWindSpeed().equals("High") && suitableCount > 3)) suitableCount++;  
        if (analysis.getRain1h().equals("Minimal")) suitableCount++;
        if (analysis.getCloudsAll().equals("Moderate")) suitableCount++; 
    
        // Classify as suitable if at least 4 of the conditions are met
        return suitableCount >= 4 ? "Suitable for diving." : "Not suitable for diving.";
    }

    
}
