package com.project.rest.coral_growth_monitor_service.service.api;

import com.project.rest.coral_growth_monitor_service.dto.ExampleDTO;
import com.project.rest.coral_growth_monitor_service.repository.ExampleRepository;

import java.util.List;


public interface ExampleService {
    public List<ExampleDTO> getAllExamples();
}
