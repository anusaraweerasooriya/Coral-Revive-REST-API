package com.project.rest.resource_allocation_service.dto;

import lombok.Data;

@Data
public class LaborDTO {
    private String name;
    private double weight;
    private double height;
    private double breathingCapacity;
    private String skills;
    private String experiences;
}
