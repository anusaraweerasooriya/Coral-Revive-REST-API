package com.project.rest.resource_allocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resources")
public class Resource {

    @Id
    private String id;
    private String name;
    private String imageUrl;
    private int quantity;
}
