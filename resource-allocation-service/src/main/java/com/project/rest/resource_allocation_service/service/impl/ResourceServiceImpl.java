package com.project.rest.resource_allocation_service.service.impl;

import com.project.rest.resource_allocation_service.model.Resource;
import com.project.rest.resource_allocation_service.repository.ResourceRepository;
import com.project.rest.resource_allocation_service.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource getResourceById(String id) {
        return resourceRepository.findById(id).orElse(null);
    }

    @Override
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Resource updateResource(String id, Resource resource) {
        Optional<Resource> existingResource = resourceRepository.findById(id);
        if (existingResource.isPresent()) {
            Resource updatedResource = existingResource.get();
            updatedResource.setName(resource.getName());
            updatedResource.setImageUrl(resource.getImageUrl());
            updatedResource.setQuantity(resource.getQuantity());
            return resourceRepository.save(updatedResource);
        }
        return null;
    }

    @Override
    public void deleteResource(String id) {
        resourceRepository.deleteById(id);
    }
}
