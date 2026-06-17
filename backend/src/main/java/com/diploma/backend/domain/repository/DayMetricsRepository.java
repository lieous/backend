package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.DayMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DayMetricsRepository extends JpaRepository<DayMetricsEntity, String> {
    List<DayMetricsEntity> findByUserIdAndUpdatedAtGreaterThan(String userId, Long timestamp);

    
    List<DayMetricsEntity> findByUserIdAndDateBetweenOrderByDateAsc(String userId, String startDate, String endDate);
}