package com.project.rest.coral_growth_monitor_service.service.api;

import com.project.rest.coral_growth_monitor_service.model.CoralBed;

import java.util.List;

public interface CoralService {
    CoralBed addCoralBed(CoralBed coralBed);

    CoralBed addCoralLocation(String coralBedId, CoralBed.CoralLocation coralLocation);

    List<CoralBed> getCoralBedsByProjectId(String projectId);

    CoralBed addGrowthHistory(String bedId, String coralLocationId, CoralBed.CoralLocation.GrowthHistory growthHistory);

    CoralBed.CoralLocation getCoralLocationById(String coralLocationId);
}
