package com.project.rest.scheduling_service.dto;

import com.project.rest.scheduling_service.model.Schedule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduledProjectForDiverDTO {
    private String id;
    private String category;
    private Schedule.GeoLocation siteLocation;
    private int numberOfReefBeds;
    private int numberOfReefSegments;
}
