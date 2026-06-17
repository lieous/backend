package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.HabitDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitDictRepository extends JpaRepository<HabitDictEntity, String> {
    List<HabitDictEntity> findByUserIdAndUpdatedAtGreaterThan(String userId, Long timestamp);
}