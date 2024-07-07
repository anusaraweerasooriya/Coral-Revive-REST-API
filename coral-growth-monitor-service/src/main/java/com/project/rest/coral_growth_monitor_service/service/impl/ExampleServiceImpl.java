package com.project.rest.coral_growth_monitor_service.service.impl;

import com.project.rest.coral_growth_monitor_service.dto.ExampleDTO;
import com.project.rest.coral_growth_monitor_service.dto.ExampleDTOMapper;
import com.project.rest.coral_growth_monitor_service.model.Example;
import com.project.rest.coral_growth_monitor_service.repository.ExampleRepository;
import com.project.rest.coral_growth_monitor_service.service.api.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExampleServiceImpl implements ExampleService {

    @Autowired
    private ExampleRepository exampleRepository;

    @Autowired
    private ExampleDTOMapper exampleDTOMapper;

    @Override
    public List<ExampleDTO> getAllExamples() {
        List<Example> examples =  new ArrayList<>();

//        for(int i =0; i<10; i++) {
//            Example example = new Example();
//            example.setName("Example "+ i+1);
//            example.setDescription("Example Description "+ i+1);
//
//            exampleRepository.save(example);
//        }

        try {
            examples = exampleRepository.findAll();
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }

        return examples.stream()
                .map(exampleDTOMapper::convertToDTO)
                .collect(Collectors.toList());

    }

}
