package com.project.rest.coral_growth_monitor_service.service.impl;

import com.project.rest.coral_growth_monitor_service.model.CoralBed;
import com.project.rest.coral_growth_monitor_service.repository.CoralRepository;
import com.project.rest.coral_growth_monitor_service.service.api.CoralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoralServiceImpl implements CoralService {

    @Autowired
    private CoralRepository coralRepository;

    @Override
    public CoralBed addCoralBed(CoralBed coralBed) {
        return coralRepository.save(coralBed);
    }

    @Override
    public CoralBed addCoralLocation(String coralBedId, CoralBed.CoralLocation coralLocation) {
        Optional<CoralBed> optionalCoralBed = coralRepository.findById(coralBedId);

        if (optionalCoralBed.isPresent()) {
            CoralBed coralBed = optionalCoralBed.get();
            coralLocation.setCreatedDate(new Date());
            coralLocation.setId(UUID.randomUUID().toString());
            coralBed.getCoralLocations().add(coralLocation);
            return coralRepository.save(coralBed);
        } else {
            throw new RuntimeException("Coral Bed not found with id: " + coralBedId);
        }
    }

    @Override
    public List<CoralBed> getCoralBedsByProjectId(String projectId) {
        return coralRepository.findByProjectId(projectId);
    }

    @Override
    public CoralBed addGrowthHistory(String bedId, String coralLocationId, CoralBed.CoralLocation.GrowthHistory growthHistory) {
        // Find the coral bed by its id
        Optional<CoralBed> optionalCoralBed = coralRepository.findById(bedId);

        if (optionalCoralBed.isPresent()) {
            CoralBed coralBed = optionalCoralBed.get();

            coralBed.getCoralLocations().stream()
                    .filter(coralLocation -> coralLocation.getId().equals(coralLocationId))
                    .findFirst()
                    .ifPresent(coralLocation -> {
                        if (coralLocation.getGrowthHistory() == null) {
                            coralLocation.setGrowthHistory(new ArrayList<>());
                        }

                        growthHistory.setId(UUID.randomUUID().toString());
                        growthHistory.setCreatedDate(new Date());

                        coralLocation.getGrowthHistory().add(growthHistory);
                        coralLocation.setUpdatedDate(new java.util.Date());
                    });
            // Save the updated coral bed
            return coralRepository.save(coralBed);
        } else {
            throw new RuntimeException("Coral Bed not found with id: " + bedId);
        }
    }

    @Override
    public CoralBed.CoralLocation getCoralLocationById(String coralLocationId) {

        for (CoralBed coralBed : coralRepository.findAll()) {

            Optional<CoralBed.CoralLocation> coralLocation = coralBed.getCoralLocations().stream()
                    .filter(location -> location.getId() != null && location.getId().equals(coralLocationId))
                    .findFirst();

            if (coralLocation.isPresent()) {
                return coralLocation.get();
            }
        }

        throw new RuntimeException("Coral Location not found with id: " + coralLocationId);
    }

}
