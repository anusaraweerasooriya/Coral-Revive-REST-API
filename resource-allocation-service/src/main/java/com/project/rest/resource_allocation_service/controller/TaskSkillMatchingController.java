package com.project.rest.resource_allocation_service.controller;

import com.project.rest.resource_allocation_service.dto.LaborDTO;
import com.project.rest.resource_allocation_service.dto.TaskAssignmentRequest;
import com.project.rest.resource_allocation_service.service.TaskSkillMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/skill-matching")
public class TaskSkillMatchingController {

    @Autowired
    private TaskSkillMatchingService taskSkillMatchingService;

    @PostMapping("/assign-tasks")
    public Map<String, String> assignTasksToLaborers(@RequestBody TaskAssignmentRequest request) {

        Map<String, String> assignments = new HashMap<>();
        Map<String, Map<String, Object>> taskReqMap = request.getTaskRequirements();

        for (LaborDTO labor : request.getLaborList()) {
            Map<String, Object> laborAttributes = new HashMap<>();
            laborAttributes.put("weight", labor.getWeight());
            laborAttributes.put("height", labor.getHeight());
            laborAttributes.put("breathingCapacity", labor.getBreathingCapacity());
            laborAttributes.put("skills", labor.getSkills());
            laborAttributes.put("experiences", labor.getExperiences());

            String bestTask = taskSkillMatchingService.assignBestTaskForLabor(laborAttributes, taskReqMap);
            assignments.put(labor.getName(), bestTask);
        }

        return assignments;
    }
}
