package com.project.rest.resource_allocation_service.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class TaskSkillMatchingService {

    public Map<String, Double> calculateTaskScores(Map<String, Object> labor, Map<String, Map<String, Object>> taskReq) {
        Map<String, Double> taskScores = new HashMap<>();

        // Loop through each task and calculate score based on requirements
        for (Map.Entry<String, Map<String, Object>> entry : taskReq.entrySet()) {
            String task = entry.getKey();
            Map<String, Object> requirements = entry.getValue();
            double score = 0;
            boolean meetsConstraints = true;

            // Calculate score and check constraints
            for (Map.Entry<String, Object> req : requirements.entrySet()) {
                String factor = req.getKey();
                Object weight = req.getValue();
                if (weight instanceof Number) {
                    score += ((Number) weight).doubleValue() * (double) labor.getOrDefault(factor, 0);
                } else if (weight instanceof Map) {
                    // Handle skill or experience constraints
                    Map<String, Integer> skillReq = (Map<String, Integer>) weight;
                    for (Map.Entry<String, Integer> skill : skillReq.entrySet()) {
                        if (!labor.get("skills").toString().contains(skill.getKey())) {
                            meetsConstraints = false;  // Laborer does not meet skill constraint
                        } else {
                            score += skill.getValue();
                        }
                    }
                }
            }

            // Set score based on constraint check
            if (meetsConstraints) {
                taskScores.put(task, score);
            } else {
                taskScores.put(task, Double.NEGATIVE_INFINITY);  // Disqualify if constraints aren't met
            }
        }

        return taskScores;
    }

    public String assignBestTaskForLabor(Map<String, Object> labor, Map<String, Map<String, Object>> taskReq) {
        Map<String, Double> taskScores = calculateTaskScores(labor, taskReq);
        return taskScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
