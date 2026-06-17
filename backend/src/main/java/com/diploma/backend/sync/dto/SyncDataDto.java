package com.diploma.backend.sync.dto;

import java.util.List;
import java.util.Map;

public record SyncDataDto(
        Long lastSyncTimestamp,
        Map<String, Object> profile,
        Map<String, Object> settings,

        List<DayMetricDto> metrics,
        List<GoalDto> goals,

        List<HabitDictDto> habitsDict,
        List<HabitLogDto> habitLogs,

        // НОВЫЕ ПОЛЯ СПРАВОЧНИКОВ
        List<SymptomDictDto> symptomDict,
        List<MoodDictDto> moodDict,

        List<SymptomLogDto> symptomLogs
) {
    public record DayMetricDto(
            String id, String date, Integer steps, Float waterLiters,
            Integer energyKcalConsumed, Integer energyKcalBurned, Integer sleepMinutes,
            Integer proteinGrams, Integer carbGrams, Integer fatGrams,
             Float glucose, Integer systolicBp, Integer diastolicBp,
            String sleepQuality, String moodEmotion, String diaryText,
            Long updatedAt, Long deletedAt
    ) {}

    public record GoalDto(
            String id, String title, String date, String time,
            Boolean isCompleted, Long updatedAt, Long deletedAt
    ) {}

    public record HabitDictDto(
            String id, String name, String iconName, String colorHex,
            Boolean isActive, Long updatedAt
    ) {}

    public record HabitLogDto(
            String habitId, String date, Boolean isCompleted, Long updatedAt
    ) {}

    // НОВЫЙ DTO
    public record SymptomDictDto(
            Long id, String name, String iconName, Boolean isActive, Long updatedAt
    ) {}

    // НОВЫЙ DTO
    public record MoodDictDto(
            String id, String name, String iconName, Boolean isActive, Long updatedAt
    ) {}

    // ИЗМЕНЕН DTO (isActive вместо deletedAt)
    public record SymptomLogDto(
            String id, Long symptomId, String date, Boolean isActive, Long updatedAt
    ) {}
}