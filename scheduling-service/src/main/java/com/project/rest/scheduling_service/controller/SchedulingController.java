package com.project.rest.scheduling_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchedulingController {

    @GetMapping("/scheduling-service")
    public ResponseEntity<?> initService() {
        return new ResponseEntity<>("Scheduling Service Initialized", HttpStatus.OK);

    }
}
