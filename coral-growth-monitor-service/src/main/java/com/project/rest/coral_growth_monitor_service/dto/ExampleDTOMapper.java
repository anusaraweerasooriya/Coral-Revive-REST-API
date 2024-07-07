package com.project.rest.coral_growth_monitor_service.dto;

import com.project.rest.coral_growth_monitor_service.model.Example;
import org.springframework.stereotype.Component;

@Component
public class ExampleDTOMapper {

    public ExampleDTO convertToDTO(Example example) {
        if (example == null) {
            return null;
        }
        return new ExampleDTO(
                example.getId(),
                example.getName(),
                example.getDescription()
        );
    }

    public Example convertToEntity(ExampleDTO exampleDTO) {
        if (exampleDTO == null) {
            return null;
        }
        return new Example(
                exampleDTO.getId(),
                exampleDTO.getName(),
                exampleDTO.getDescription()
        );
    }
}
