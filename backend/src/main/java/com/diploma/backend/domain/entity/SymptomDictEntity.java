package com.diploma.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "symptoms_dict")
@Getter
@Setter
public class SymptomDictEntity {

    @Id
    @Column(name = "id")
    private Long id; 

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "icon_name", nullable = false, length = 100)
    private String iconName;

    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
}