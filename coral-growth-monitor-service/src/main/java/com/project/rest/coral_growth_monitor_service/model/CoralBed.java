package com.project.rest.coral_growth_monitor_service.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "coral_bed")
public class CoralBed {

    @Id
    private String id;
    private String projectId;
    private Location location;
    private String coralBedName;
    private String noOfCoralLocations;
    private List<CoralLocation> coralLocations;

    @Data
    @Getter
    @Setter
    public static class Location {
        private double longitude;
        private double latitude;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CoralLocation {
        @Id
        private String id;
        private String coralName;
        private Coordinates coralLocation;
        @CreatedDate
        private Date createdDate;
        @LastModifiedDate
        private Date updatedDate;
        private List<GrowthHistory> growthHistory;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class GrowthHistory {
            @Id
            private  String id;
            private String growth;
            private String growthStage;
            private String growthRate;
            private String polypCount;
            private String area;
            private String imageUrl;
            @CreatedDate
            private Date createdDate;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Coordinates {
            private String row;
            private String column;
        }
    }
}


