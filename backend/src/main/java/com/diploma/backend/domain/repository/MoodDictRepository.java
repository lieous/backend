package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.MoodDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoodDictRepository extends JpaRepository<MoodDictEntity, String> {

    
    List<MoodDictEntity> findByUpdatedAtGreaterThan(Long timestamp);
}