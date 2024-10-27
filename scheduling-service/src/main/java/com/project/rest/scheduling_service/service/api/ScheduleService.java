package com.project.rest.scheduling_service.service.api;
import com.project.rest.scheduling_service.dto.ResourceEstimationRequest;
import com.project.rest.scheduling_service.dto.ScheduledProjectForDiverDTO;
import com.project.rest.scheduling_service.model.Schedule;

import java.util.List;
import java.time.ZonedDateTime;

public interface ScheduleService {
    Schedule createSchedule(Schedule schedule);
    Schedule updateScheduledDate(String scheduleId, ZonedDateTime scheduledDate);
    List<Schedule> getPrioritizedSchedules();
    List<ScheduledProjectForDiverDTO> getScheduledProjectsForDiver();
    long countSchedulesByStatus(Schedule.Status status);
    Schedule updateResourceEstimation(String scheduleId, ResourceEstimationRequest request);
}
