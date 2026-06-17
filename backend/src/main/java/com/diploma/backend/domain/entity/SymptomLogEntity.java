package com.diploma.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "symptom_logs")
@Getter
@Setter
public class SymptomLogEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id; 

    @Column(name = "symptom_id", nullable = false)
    private Long symptomId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "date_str", nullable = false)
    private String date; 

    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
}