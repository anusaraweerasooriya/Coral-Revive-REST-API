package com.project.rest.coral_growth_monitor_service.controller;

import com.project.rest.coral_growth_monitor_service.model.CoralBed;
import com.project.rest.coral_growth_monitor_service.service.api.CoralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coral-growth-monitor-service")
public class CoralServiceController {

    @Autowired
    private CoralService coralService;

    @PostMapping("/add-coral-bed")
    public ResponseEntity<CoralBed> addCoralBed(@RequestBody CoralBed coralBed) {
        CoralBed createdCoralBed = coralService.addCoralBed(coralBed);
        return new ResponseEntity<>(createdCoralBed, HttpStatus.CREATED);
    }

    @PostMapping("/add-coral-location/{coralBedId}/locations")
    public ResponseEntity<CoralBed> addCoralLocation(
            @PathVariable String coralBedId,
            @RequestBody CoralBed.CoralLocation coralLocation) {
        CoralBed updatedCoralBed = coralService.addCoralLocation(coralBedId, coralLocation);
        return new ResponseEntity<>(updatedCoralBed, HttpStatus.OK);
    }

    @GetMapping("/get-coral-beds/{projectId}")
    public List<CoralBed> getCoralBedsByProjectId(@PathVariable String projectId) {
        return coralService.getCoralBedsByProjectId(projectId);
    }

    @PostMapping("/add-growth-history/beds/{bedId}/locations/{coralLocationId}")
    public ResponseEntity<CoralBed> addGrowthHistory(@PathVariable String bedId,
                                                     @PathVariable String coralLocationId,
                                                     @RequestBody CoralBed.CoralLocation.GrowthHistory growthHistory) {
        CoralBed updatedCoralBed = coralService.addGrowthHistory(bedId, coralLocationId, growthHistory);
        return ResponseEntity.ok(updatedCoralBed);
    }

    @GetMapping("/get-coral-location/{id}")
    public CoralBed.CoralLocation getCoralLocationById(@PathVariable("id") String coralLocationId) {
        return coralService.getCoralLocationById(coralLocationId);
    }

}
