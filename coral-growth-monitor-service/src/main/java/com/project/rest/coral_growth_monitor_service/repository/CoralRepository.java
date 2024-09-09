package com.project.rest.coral_growth_monitor_service.repository;

import com.project.rest.coral_growth_monitor_service.model.CoralBed;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CoralRepository extends MongoRepository<CoralBed, String> {
    List<CoralBed> findByProjectId(String projectId);
}
