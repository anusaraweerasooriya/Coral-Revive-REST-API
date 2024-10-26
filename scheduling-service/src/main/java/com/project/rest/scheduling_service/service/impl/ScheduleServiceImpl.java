package com.project.rest.scheduling_service.service.impl;

import com.project.rest.scheduling_service.model.Schedule;
import com.project.rest.scheduling_service.repository.ScheduleRepository;
import com.project.rest.scheduling_service.service.api.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        schedule.setCreatedDate(Date.from(ZonedDateTime.now().toInstant()));  // Convert to Date
        schedule.setLastUpdated(Date.from(ZonedDateTime.now().toInstant()));  // Convert to Date
        schedule.setStatus(Schedule.Status.Pending_Resource_Availability);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateScheduledDate(String scheduleId, ZonedDateTime scheduledDate) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setScheduledDate(Date.from(scheduledDate.toInstant()));
            schedule.setStatus(Schedule.Status.Scheduled);
            schedule.setLastUpdated(Date.from(ZonedDateTime.now().toInstant())); 
            return scheduleRepository.save(schedule);
        } else {
            throw new RuntimeException("Schedule not found with id: " + scheduleId);
        }
    }

    @Override
    public Schedule updateResourceAvailability(String scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setStatus(Schedule.Status.Pending_Date_Schedule);
            schedule.setLastUpdated(Date.from(ZonedDateTime.now().toInstant())); 
            return scheduleRepository.save(schedule);
        } else {
            throw new RuntimeException("Schedule not found with id: " + scheduleId);
        }
    }

    @Override
    public long countSchedulesByStatus(Schedule.Status status) {
        return scheduleRepository.countByStatus(status);
    }

    @Override
    public List<Schedule> getPrioritizedSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();

        // Prepare the request payload for the Flask API
        List<Map<String, Object>> requestPayload = schedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", schedule.getId());
            map.put("urgency", schedule.getUrgency());
            map.put("impact", schedule.getImpact());
            map.put("status", schedule.getStatus().toString());
            return map;
        }).collect(Collectors.toList());

        // Call the Flask service to get the prioritized schedules
        List<Map<String, Object>> prioritizedSchedules = fetchPrioritizedSchedulesFromFlask(requestPayload);

        // Update schedules with the received priority ranks
        for (Map<String, Object> prioritizedSchedule : prioritizedSchedules) {
            String id = (String) prioritizedSchedule.get("id");
            int priorityRank = (int) prioritizedSchedule.get("priorityRank");

            Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
            if (optionalSchedule.isPresent()) {
                Schedule schedule = optionalSchedule.get();
                schedule.setPriorityRank(priorityRank);
                schedule.setLastUpdated(new Date());
                scheduleRepository.save(schedule);
            }
        }

        // Return all schedules ordered by priority rank in descending order
        return scheduleRepository.findAllByOrderByPriorityRankAsc();
    }

    private List<Map<String, Object>> fetchPrioritizedSchedulesFromFlask(List<Map<String, Object>> schedules) {
        RestTemplate restTemplate = new RestTemplate();
    
        // Wrap the list in a map with the key 'schedules'
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("schedules", schedules);
    
        // Send request to Flask server and get the response
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> prioritizedSchedules = restTemplate.postForObject(
            flaskApiUrl + "/api/schedule/prioritize",
            requestPayload,  
            List.class 
        );
    
        return prioritizedSchedules;
    }
}
