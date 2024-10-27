package com.project.rest.scheduling_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class LaborDTO {
    private String name;
    private double weight;
    private double bmi;
    private double height;
    private String strength;
    private double breathingCapacity;
    private List<String> experiences;
    private List<String> skills;
    private String task;
    private double oxygenCapacity;
}
