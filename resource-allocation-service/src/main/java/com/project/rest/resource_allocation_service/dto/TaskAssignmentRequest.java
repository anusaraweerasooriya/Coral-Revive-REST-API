package com.project.rest.resource_allocation_service.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TaskAssignmentRequest {
    private List<LaborDTO> laborList;
    private Map<String, Map<String, Object>> taskRequirements;
}
