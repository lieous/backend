package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, String> {
    List<GoalEntity> findByUserIdAndUpdatedAtGreaterThan(String userId, Long timestamp);
    List<GoalEntity> findByUserIdAndDateBetween(String userId, String startDate, String endDate);

}