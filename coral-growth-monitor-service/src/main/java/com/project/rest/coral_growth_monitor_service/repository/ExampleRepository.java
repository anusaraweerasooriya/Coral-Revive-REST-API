package com.project.rest.coral_growth_monitor_service.repository;

import com.project.rest.coral_growth_monitor_service.model.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExampleRepository extends MongoRepository<Example, String> {

}
