package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.SymptomLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SymptomLogRepository extends JpaRepository<SymptomLogEntity, String> {
    List<SymptomLogEntity> findByUserIdAndUpdatedAtGreaterThan(String userId, Long timestamp);
    List<SymptomLogEntity> findByUserIdAndDateBetween(String userId, String startDate, String endDate);

}