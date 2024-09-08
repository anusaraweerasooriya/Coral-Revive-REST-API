package com.project.rest.scheduling_service.service.api;
import com.project.rest.scheduling_service.model.Schedule;

import java.util.List;
import java.time.ZonedDateTime;

public interface ScheduleService {
    Schedule createSchedule(Schedule schedule);
    Schedule updateScheduledDate(String scheduleId, ZonedDateTime scheduledDate);
    List<Schedule> getPrioritizedSchedules();

    long countSchedulesByStatus(Schedule.Status status);
}
