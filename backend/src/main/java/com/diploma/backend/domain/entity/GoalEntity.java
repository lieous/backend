package com.diploma.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
public class GoalEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String title;

    @Column(name = "date_str")
    private String date;

    @Column(name = "time_str")
    private String time;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "updated_at")
    private Long updatedAt = 0L;

    @Column(name = "deleted_at")
    private Long deletedAt;
}