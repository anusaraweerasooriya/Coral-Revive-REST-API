package com.project.rest.coral_growth_monitor_service.controller;

import com.project.rest.coral_growth_monitor_service.dto.ExampleDTO;
import com.project.rest.coral_growth_monitor_service.service.api.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/coral-growth-monitor-service/example-request")
    public ResponseEntity<?> getAllExamples() {
        List<ExampleDTO> examples = exampleService.getAllExamples();
        return new ResponseEntity<>(examples, examples.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);

    }

}
