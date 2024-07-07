package com.project.rest.coral_growth_monitor_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "example")
public class Example {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
    
    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public Example(String id, String name, String description) {
    }
}
