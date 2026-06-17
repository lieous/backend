package com.diploma.backend.sync.service;

import com.diploma.backend.domain.entity.*;
import com.diploma.backend.domain.repository.*;
import com.diploma.backend.sync.dto.SyncDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final UserRepository userRepository;
    private final DayMetricsRepository metricsRepository;
    private final GoalRepository goalRepository;
    private final HabitDictRepository habitDictRepository;
    private final HabitLogRepository habitLogRepository;
    private final SymptomLogRepository symptomLogRepository;
    private final SymptomDictRepository symptomDictRepository;
    private final MoodDictRepository moodDictRepository;

    @Transactional(readOnly = true)
    public SyncDataDto pullData(String userId, Long lastSyncTimestamp) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SyncDataDto.DayMetricDto> metrics = metricsRepository
                .findByUserIdAndUpdatedAtGreaterThan(userId, lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.GoalDto> goals = goalRepository
                .findByUserIdAndUpdatedAtGreaterThan(userId, lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.HabitDictDto> habitsDict = habitDictRepository
                .findByUserIdAndUpdatedAtGreaterThan(userId, lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.HabitLogDto> habitLogs = habitLogRepository
                .findByIdUserIdAndUpdatedAtGreaterThan(userId, lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.SymptomLogDto> symptomLogs = symptomLogRepository
                .findByUserIdAndUpdatedAtGreaterThan(userId, lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.SymptomDictDto> symptomDict = symptomDictRepository
                .findByUpdatedAtGreaterThan(lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        List<SyncDataDto.MoodDictDto> moodDict = moodDictRepository
                .findByUpdatedAtGreaterThan(lastSyncTimestamp).stream()
                .map(this::mapToDto).toList();

        return new SyncDataDto(
                System.currentTimeMillis(),
                user.getProfileData(),
                user.getSettings(),
                metrics,
                goals,
                habitsDict,
                habitLogs,
                symptomDict,
                moodDict,
                symptomLogs
        );
    }

    @Transactional
    public void pushData(String userId, SyncDataDto data) {

        UserEntity user = userRepository.findById(userId).orElse(new UserEntity());
        user.setId(userId);
        if (user.getEmail() == null) user.setEmail(userId + "@example.com");

        if (data.profile() != null) user.setProfileData(data.profile());
        if (data.settings() != null) user.setSettings(data.settings());
        userRepository.save(user);

        // 1. Метрики дня
        if (data.metrics() != null) {
            for (SyncDataDto.DayMetricDto dto : data.metrics()) {
                DayMetricsEntity existing = metricsRepository.findById(dto.id()).orElse(null);
                if (existing == null || dto.updatedAt() > existing.getUpdatedAt()) {
                    metricsRepository.save(mapToEntity(dto, userId));
                }
            }
        }

        // 2. Цели
        if (data.goals() != null) {
            for (SyncDataDto.GoalDto dto : data.goals()) {
                GoalEntity existing = goalRepository.findById(dto.id()).orElse(null);
                if (existing == null || dto.updatedAt() > existing.getUpdatedAt()) {
                    goalRepository.save(mapToEntity(dto, userId));
                }
            }
        }

        // 3. Справочник привычек
        if (data.habitsDict() != null) {
            for (SyncDataDto.HabitDictDto dto : data.habitsDict()) {
                HabitDictEntity existing = habitDictRepository.findById(dto.id()).orElse(null);
                if (existing == null || dto.updatedAt() > existing.getUpdatedAt()) {
                    habitDictRepository.save(mapToEntity(dto, userId));
                }
            }
        }

        // 4. Логи привычек
        if (data.habitLogs() != null) {
            for (SyncDataDto.HabitLogDto dto : data.habitLogs()) {
                HabitLogEntity.HabitLogId id = new HabitLogEntity.HabitLogId(dto.habitId(), userId, dto.date());
                HabitLogEntity existing = habitLogRepository.findById(id).orElse(null);
                if (existing == null || dto.updatedAt() > existing.getUpdatedAt()) {
                    habitLogRepository.save(mapToEntity(dto, userId));
                }
            }
        }

        // 5. Логи симптомов
        if (data.symptomLogs() != null) {
            for (SyncDataDto.SymptomLogDto dto : data.symptomLogs()) {
                SymptomLogEntity existing = symptomLogRepository.findById(dto.id()).orElse(null);
                if (existing == null || dto.updatedAt() > existing.getUpdatedAt()) {
                    symptomLogRepository.save(mapToEntity(dto, userId));
                }
            }
        }
    }

    // =====================================================================================
    // МАППЕРЫ (ENTITY <-> DTO)
    // =====================================================================================

    private SyncDataDto.DayMetricDto mapToDto(DayMetricsEntity e) {
        return new SyncDataDto.DayMetricDto(
                e.getId(), e.getDate(), e.getSteps(), e.getWaterLiters(),
                e.getEnergyKcalConsumed(), e.getEnergyKcalBurned(), e.getSleepMinutes(),
                e.getProteinGrams(), e.getCarbGrams(), e.getFatGrams(),
                e.getGlucose(), e.getSystolicBp(), e.getDiastolicBp(),
                e.getSleepQuality(), e.getMoodEmotion(), e.getDiaryText(),
                e.getUpdatedAt(), e.getDeletedAt()
        );
    }

    private DayMetricsEntity mapToEntity(SyncDataDto.DayMetricDto dto, String userId) {
        DayMetricsEntity e = new DayMetricsEntity();
        e.setId(dto.id()); e.setUserId(userId); e.setDate(dto.date()); e.setSteps(dto.steps());
        e.setWaterLiters(dto.waterLiters()); e.setEnergyKcalConsumed(dto.energyKcalConsumed());
        e.setEnergyKcalBurned(dto.energyKcalBurned()); e.setSleepMinutes(dto.sleepMinutes());
        e.setProteinGrams(dto.proteinGrams()); e.setCarbGrams(dto.carbGrams()); e.setFatGrams(dto.fatGrams());
        e.setGlucose(dto.glucose()); e.setSystolicBp(dto.systolicBp());
        e.setDiastolicBp(dto.diastolicBp()); e.setSleepQuality(dto.sleepQuality());
        e.setMoodEmotion(dto.moodEmotion()); e.setDiaryText(dto.diaryText());
        e.setUpdatedAt(dto.updatedAt()); e.setDeletedAt(dto.deletedAt());
        // Флаг isSynced на бэкенде нам не нужен, он нужен только на Android
        return e;
    }

    private SyncDataDto.GoalDto mapToDto(GoalEntity e) {
        return new SyncDataDto.GoalDto(e.getId(), e.getTitle(), e.getDate(), e.getTime(), e.getIsCompleted(), e.getUpdatedAt(), e.getDeletedAt());
    }

    private GoalEntity mapToEntity(SyncDataDto.GoalDto dto, String userId) {
        GoalEntity e = new GoalEntity();
        e.setId(dto.id()); e.setUserId(userId); e.setTitle(dto.title()); e.setDate(dto.date());
        e.setTime(dto.time()); e.setIsCompleted(dto.isCompleted()); e.setUpdatedAt(dto.updatedAt()); e.setDeletedAt(dto.deletedAt());
        return e;
    }

    private SyncDataDto.HabitDictDto mapToDto(HabitDictEntity e) {
        return new SyncDataDto.HabitDictDto(e.getId(), e.getName(), e.getIconName(), e.getColorHex(), e.getIsActive(), e.getUpdatedAt());
    }

    private HabitDictEntity mapToEntity(SyncDataDto.HabitDictDto dto, String userId) {
        HabitDictEntity e = new HabitDictEntity();
        e.setId(dto.id()); e.setUserId(userId); e.setName(dto.name()); e.setIconName(dto.iconName());
        e.setColorHex(dto.colorHex()); e.setIsActive(dto.isActive()); e.setUpdatedAt(dto.updatedAt());
        return e;
    }

    private SyncDataDto.HabitLogDto mapToDto(HabitLogEntity e) {
        return new SyncDataDto.HabitLogDto(e.getId().getHabitId(), e.getId().getDateStr(), e.getIsCompleted(), e.getUpdatedAt());
    }

    private HabitLogEntity mapToEntity(SyncDataDto.HabitLogDto dto, String userId) {
        HabitLogEntity e = new HabitLogEntity();
        e.setId(new HabitLogEntity.HabitLogId(dto.habitId(), userId, dto.date()));
        e.setIsCompleted(dto.isCompleted()); e.setUpdatedAt(dto.updatedAt());
        return e;
    }

    private SyncDataDto.SymptomDictDto mapToDto(SymptomDictEntity e) {
        return new SyncDataDto.SymptomDictDto(e.getId(), e.getName(), e.getIconName(), e.getIsActive(), e.getUpdatedAt());
    }

    private SyncDataDto.MoodDictDto mapToDto(MoodDictEntity e) {
        return new SyncDataDto.MoodDictDto(e.getId(), e.getName(), e.getIconName(), e.getIsActive(), e.getUpdatedAt());
    }

    private SyncDataDto.SymptomLogDto mapToDto(SymptomLogEntity e) {
        return new SyncDataDto.SymptomLogDto(e.getId(), e.getSymptomId(), e.getDate(), e.getIsActive(), e.getUpdatedAt());
    }

    private SymptomLogEntity mapToEntity(SyncDataDto.SymptomLogDto dto, String userId) {
        SymptomLogEntity e = new SymptomLogEntity();
        e.setId(dto.id());
        e.setUserId(userId);
        e.setSymptomId(dto.symptomId());
        e.setDate(dto.date());
        e.setIsActive(dto.isActive());
        e.setUpdatedAt(dto.updatedAt());
        return e;
    }
}