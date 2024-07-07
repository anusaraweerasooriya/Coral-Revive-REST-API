package com.project.rest.resource_allocation_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceAllocationController {

    @GetMapping("/resource-allocation-service")
    public ResponseEntity<?> initService() {
        return new ResponseEntity<>("Resource Allocation Service Initialized", HttpStatus.OK);

    }
}
