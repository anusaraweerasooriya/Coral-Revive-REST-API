package com.project.rest.scheduling_service.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import com.project.rest.scheduling_service.dto.ScheduledProjectForDiverDTO;
import com.project.rest.scheduling_service.model.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
   
    long countByStatus(Schedule.Status status);
    List<Schedule> findAllByOrderByPriorityRankAsc();

    @Query(value = "{ 'status': ?0 }", fields = "{ 'id': 1, 'category': 1, 'siteLocation': 1, 'numberOfReefBeds': 1, 'numberOfReefSegments': 1 }")
    List<ScheduledProjectForDiverDTO> findByStatus(Schedule.Status status, Sort sort);
}
