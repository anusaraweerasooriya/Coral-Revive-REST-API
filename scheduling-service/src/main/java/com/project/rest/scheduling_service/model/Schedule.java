package com.project.rest.scheduling_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

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

    // New fields for resource estimation
    private double area;
    private double depth;
    private String waterCurrent;
    private double temperature;
    private int numberOfReefBeds;
    private int manpowerRequired;
    private int numberOfReefSegments;
    private int numberOfBoats;
    private int numberOfDivingKits;
    private double amountOfBoundingGlue;
    private int manpowerBoatOperation;
    private int manpowerCarryingReefBowls;
    private int manpowerPlantingCorals;
    private int manpowerSitePreparation;

    // New field for labor details
    private List<Labor> labors;

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

    @Data
    public static class Labor {
        private String name;
        private double weight;
        private double bmi;
        private double height;
        private String strength;
        private double breathingCapacity;
        private List<String> experiences; // List of experiences
        private List<String> skills; // List of skills
        private String task;
        private double oxygenCapacity;
    }   private String task;
}
