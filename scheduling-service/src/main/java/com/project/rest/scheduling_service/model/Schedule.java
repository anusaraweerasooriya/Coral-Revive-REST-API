package com.project.rest.scheduling_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String id;
    private String category;
    private GeoLocation siteLocation;
    private int urgency;
    private int impact;
    private int priorityRank; 
    private Date scheduledDate;  
    private Status status;
    private Date createdDate;    
    private Date lastUpdated;   

    // Enum for status
    public enum Status {
        Pending_Resource_Availability,
        Pending_Date_Schedule,
        Scheduled
    }

    // Nested classes for embedded objects
    @Data
    public static class GeoLocation {
        private double latitude;
        private double longitude;
    }
}
