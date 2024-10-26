package com.project.rest.resource_allocation_service.repository;

import com.project.rest.resource_allocation_service.model.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends MongoRepository<Resource, String> {
}
