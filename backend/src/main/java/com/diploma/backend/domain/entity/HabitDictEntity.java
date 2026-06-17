package com.diploma.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "habits_dict")
@Getter
@Setter
@NoArgsConstructor
public class HabitDictEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String name;

    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "color_hex")
    private String colorHex;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "updated_at")
    private Long updatedAt = 0L;
}