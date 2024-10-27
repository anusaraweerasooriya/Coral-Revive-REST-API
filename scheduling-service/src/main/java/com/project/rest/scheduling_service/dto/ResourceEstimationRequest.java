package com.project.rest.scheduling_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResourceEstimationRequest {
    private double area;
    private double depth;
    private String waterCurrent;
    private double temperature;
    private int numberOfReefBeds;
    private int manpowerRequired;
    private int numberOfReefSegments;
    private int numberOfBoats;
    private int numberOfDivingKits;
    private double amountOfBoundingGlue;
    private int manpowerBoatOperation;
    private int manpowerCarryingReefBowls;
    private int manpowerPlantingCorals;
    private int manpowerSitePreparation;
    private List<LaborDTO> labors;
}
