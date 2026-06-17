package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.SymptomDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymptomDictRepository extends JpaRepository<SymptomDictEntity, Long> {

    
    List<SymptomDictEntity> findByUpdatedAtGreaterThan(Long timestamp);
}