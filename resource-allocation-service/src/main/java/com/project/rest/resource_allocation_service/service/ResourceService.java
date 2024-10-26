package com.project.rest.resource_allocation_service.service;

import com.project.rest.resource_allocation_service.model.Resource;
import java.util.List;

public interface ResourceService {
    List<Resource> getAllResources();
    Resource getResourceById(String id);
    Resource createResource(Resource resource);
    Resource updateResource(String id, Resource resource);
    void deleteResource(String id);
}
