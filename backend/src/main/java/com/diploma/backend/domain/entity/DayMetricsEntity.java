package com.diploma.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "day_metrics")
@Getter
@Setter
@NoArgsConstructor
public class DayMetricsEntity {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "date_str", nullable = false)
    private String date;

    private Integer steps = 0;

    @Column(name = "water_liters")
    private Float waterLiters = 0f;

    @Column(name = "energy_kcal_consumed")
    private Integer energyKcalConsumed = 0;

    @Column(name = "energy_kcal_burned")
    private Integer energyKcalBurned = 0;

    @Column(name = "sleep_minutes")
    private Integer sleepMinutes = 0;

    @Column(name = "protein_grams")
    private Integer proteinGrams = 0;

    @Column(name = "carb_grams")
    private Integer carbGrams = 0;

    @Column(name = "fat_grams")
    private Integer fatGrams = 0;
    

    private Float glucose;

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "sleep_quality")
    private String sleepQuality;

    @Column(name = "mood_emotion")
    private String moodEmotion;

    @Column(name = "diary_text")
    private String diaryText;

    @Column(name = "updated_at")
    private Long updatedAt = 0L;

    @Column(name = "deleted_at")
    private Long deletedAt;
}