package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.HabitLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLogEntity, HabitLogEntity.HabitLogId> {
    
    List<HabitLogEntity> findByIdUserIdAndUpdatedAtGreaterThan(String userId, Long timestamp);
    
    List<HabitLogEntity> findByIdUserIdAndIdDateStrBetween(String userId, String startDate, String endDate);
}