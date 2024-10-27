package com.project.rest.resource_allocation_service.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TaskRequirementsDTO {
    private Map<String, Map<String, Object>> taskRequirements;
}
