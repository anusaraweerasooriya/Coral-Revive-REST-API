package com.project.rest.scheduling_service.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.project.rest.scheduling_service.model.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
   
    long countByStatus(Schedule.Status status);
    List<Schedule> findAllByOrderByPriorityRankAsc();
}
