package com.project.rest.scheduling_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.rest.scheduling_service.model.Schedule;
import com.project.rest.scheduling_service.service.api.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scheduling-service/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule createdSchedule = scheduleService.createSchedule(schedule);
        return new ResponseEntity<>(createdSchedule, HttpStatus.OK);
    }

    @PatchMapping("/{scheduleId}/date")
    public ResponseEntity<Schedule> updateScheduledDate(@PathVariable String scheduleId, @RequestBody Map<String, String> request) {
        ZonedDateTime scheduledDate = ZonedDateTime.parse(request.get("date"));
        Schedule updatedSchedule = scheduleService.updateScheduledDate(scheduleId, scheduledDate);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    @GetMapping("/count/scheduled")
    public ResponseEntity<Long> countScheduled() {
        long count = scheduleService.countSchedulesByStatus(Schedule.Status.Scheduled);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/pending-date-schedule")
    public ResponseEntity<Long> countPendingDateSchedule() {
        long count = scheduleService.countSchedulesByStatus(Schedule.Status.Pending_Date_Schedule);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/prioritized")
    public ResponseEntity<List<Schedule>> getPrioritizedSchedules() {
        List<Schedule> prioritizedSchedules = scheduleService.getPrioritizedSchedules();
        return new ResponseEntity<>(prioritizedSchedules, HttpStatus.OK);
    }
}
