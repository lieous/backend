package com.diploma.backend.stats.service;

import com.diploma.backend.domain.entity.*;
import com.diploma.backend.domain.repository.*;
import com.diploma.backend.stats.dto.WeeklySummaryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final DayMetricsRepository metricsRepository;
    private final SymptomLogRepository symptomLogRepository;
    private final SymptomDictRepository symptomDictRepository;
    private final MoodDictRepository moodDictRepository;
    private final HabitLogRepository habitLogRepository;
    private final GoalRepository goalRepository;

    private final GigaChatService gigaChatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    @Data @Builder
    public static class MetricsDto {
        private Integer steps;
        private Double waterLiters;
        private Integer energyKcalConsumed;
        private Integer energyKcalBurned;
        private Integer sleepMinutes;
        private String sleepQuality;
        private Integer proteinGrams;
        private Integer carbGrams;
        private Integer fatGrams;
        private Integer systolicBp;
        private Integer diastolicBp;
        private Float glucose;
    }

    
    @Data @Builder
    public static class DisciplineDto {
        private Integer habitsTotal;
        private Integer habitsCompleted;
        private Integer goalsTotal;
        private Integer goalsCompleted;
    }

    @Data @Builder
    public static class DayTimelineDto {
        private String date;
        private MetricsDto metrics;
        private String mood;
        private List<String> symptoms;
        private String diaryText;
        private DisciplineDto discipline;
    }

    public WeeklySummaryResponse getWeeklySummary(String userId, String endDateStr) {
        LocalDate endDate = LocalDate.parse(endDateStr);
        LocalDate startDate = endDate.minusDays(6);

        String startDateStr = startDate.toString();
        String endDateStrForQuery = endDate.toString();

        log.info("Сборка данных для ИИ пользователя {} с {} по {}", userId, startDateStr, endDateStrForQuery);

        
        List<DayMetricsEntity> metrics = metricsRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, startDateStr, endDateStrForQuery);
        List<SymptomLogEntity> symptomLogs = symptomLogRepository.findByUserIdAndDateBetween(userId, startDateStr, endDateStrForQuery);
        List<HabitLogEntity> habitLogs = habitLogRepository.findByIdUserIdAndIdDateStrBetween(userId, startDateStr, endDateStrForQuery);
        List<GoalEntity> goals = goalRepository.findByUserIdAndDateBetween(userId, startDateStr, endDateStrForQuery);

        
        Map<Long, String> symptomDict = symptomDictRepository.findAll().stream()
                .collect(Collectors.toMap(SymptomDictEntity::getId, SymptomDictEntity::getName));
        Map<String, String> moodDict = moodDictRepository.findAll().stream()
                .collect(Collectors.toMap(MoodDictEntity::getId, MoodDictEntity::getName));

        
        Map<String, List<SymptomLogEntity>> symptomsByDate = symptomLogs.stream()
                .filter(SymptomLogEntity::getIsActive)
                .collect(Collectors.groupingBy(SymptomLogEntity::getDate));

        Map<String, DayMetricsEntity> metricsByDate = metrics.stream()
                .collect(Collectors.toMap(DayMetricsEntity::getDate, m -> m));

        Map<String, List<HabitLogEntity>> habitsByDate = habitLogs.stream()
                .collect(Collectors.groupingBy(h -> h.getId().getDateStr()));

        
        Map<String, List<GoalEntity>> goalsByDate = goals.stream()
                .filter(g -> g.getDeletedAt() == null)
                .collect(Collectors.groupingBy(GoalEntity::getDate));

        
        List<DayTimelineDto> weekTimeline = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            String dStr = current.toString();
            DayMetricsEntity m = metricsByDate.get(dStr);

            
            List<String> symptomNames = symptomsByDate.getOrDefault(dStr, Collections.emptyList())
                    .stream().map(s -> symptomDict.getOrDefault(s.getSymptomId(), "Неизвестно")).toList();

            
            List<HabitLogEntity> dayHabits = habitsByDate.getOrDefault(dStr, Collections.emptyList());
            int habitsTotal = dayHabits.size();
            int habitsCompleted = (int) dayHabits.stream().filter(HabitLogEntity::getIsCompleted).count();

            
            List<GoalEntity> dayGoals = goalsByDate.getOrDefault(dStr, Collections.emptyList());
            int goalsTotal = dayGoals.size();
            int goalsCompleted = (int) dayGoals.stream().filter(GoalEntity::getIsCompleted).count();

            
            weekTimeline.add(DayTimelineDto.builder()
                    .date(dStr)
                    .mood(m != null && m.getMoodEmotion() != null ? moodDict.getOrDefault(m.getMoodEmotion(), "Не указано") : "Не указано")
                    .diaryText(m != null && m.getDiaryText() != null ? m.getDiaryText() : "")
                    .symptoms(symptomNames)
                    .metrics(MetricsDto.builder()
                            .steps(m != null && m.getSteps() != null ? m.getSteps() : 0)
                            .waterLiters(m != null && m.getWaterLiters() != null ? m.getWaterLiters() : 0.0)
                            .energyKcalConsumed(m != null && m.getEnergyKcalConsumed() != null ? m.getEnergyKcalConsumed() : 0)
                            .energyKcalBurned(m != null && m.getEnergyKcalBurned() != null ? m.getEnergyKcalBurned() : 0)
                            .sleepMinutes(m != null && m.getSleepMinutes() != null ? m.getSleepMinutes() : 0)
                            .sleepQuality(m != null && m.getSleepQuality() != null ? m.getSleepQuality() : "Не указано")
                            .proteinGrams(m != null && m.getProteinGrams() != null ? m.getProteinGrams() : 0)
                            .carbGrams(m != null && m.getCarbGrams() != null ? m.getCarbGrams() : 0)
                            .fatGrams(m != null && m.getFatGrams() != null ? m.getFatGrams() : 0)
                            .systolicBp(m != null ? m.getSystolicBp() : null)
                            .diastolicBp(m != null ? m.getDiastolicBp() : null)
                            .glucose(m != null ? m.getGlucose() : null)
                            .build())
                    .discipline(DisciplineDto.builder()
                            .habitsTotal(habitsTotal)
                            .habitsCompleted(habitsCompleted)
                            .goalsTotal(goalsTotal)
                            .goalsCompleted(goalsCompleted)
                            .build())
                    .build());
            current = current.plusDays(1);
        }

        
        String timelineJson = "";
        try {
            timelineJson = objectMapper.writeValueAsString(weekTimeline);
        } catch (Exception e) {
            log.error("Ошибка сериализации таймлайна в JSON: ", e);
        }

        



        
        String prompt = "Ты — чуткий личный health-коуч и психолог в premium-приложении для здоровья. Твоя цель — проанализировать данные за 7 дней и выдать глубокие, живые и персонализированные инсайты.\n\n" +
                "ЖЕСТКИЕ ПРАВИЛА СТИЛЯ И ЛОГИКИ:\n" +
                "1. СТИЛЬ ОБЩЕНИЯ: Пиши как эмпатичный человек, обращайся на 'ты'. КАТЕГОРИЧЕСКИ ЗАПРЕЩЕН сухой медицинский или отчетный стиль. НЕ ИСПОЛЬЗУЙ слова: 'наблюдалась динамика', 'характеризовался', 'зафиксировано', 'показатели', 'свидетельствует'.\n" +
                "2. КОНКРЕТИКА ИЗ ДНЕВНИКА: Никогда не пиши абстрактное 'был стресс'. ОБЯЗАТЕЛЬНО называй конкретные события из дневника (diaryText) — из-за чего именно переживал человек? Если дневник пуст, анализируй только физику.\n" +
                "3. ТОЧНЫЕ ЦИФРЫ: В каждом блоке в построении вывода ОБЯЗАТЕЛЬНО используй конкретные метрики (шаги, граммы, минуты сна) (внедряй их в текст).\n\n" +
                "ВЫХОДНОЙ ФОРМАТ (СТРОГИЙ JSON):\n" +
                "{\n" +
                "  \"wellness_summary\": \"(6-8 предложений). Расскажи связную историю этой недели. Как события в жизни (назови их из дневника) отражались на теле и эмоциях? Сделай акцент на контрастах. ЗДЕСЬ СТРОГО ЗАПРЕЩЕНЫ ЛЮБЫЕ СОВЕТЫ, РЕКОМЕНДАЦИИ И ПРИЗЫВЫ К ДЕЙСТВИЮ. Только аналитика и теплые слова поддержки в конце.\",\n" +
                "  \"activity_insight\": \"(3-4 предложения). Свяжи конкретные цифры шагов/активности с качеством сна и настроением (конкретные цифры приведи). ВМЕСТО банального совета 'больше гуляй', предложи микро-хак, вытекающий из данных. ЗАПРЕЩЕНЫ слова 'стоит', 'рекомендуется', 'необходимо'.\",\n" +
                "  \"recovery_mind_insight\": \"(4-5 предложений). Глубокий анализ связи психики и тела. Укажи КОНКРЕТНЫЕ события из diaryText и свяжи их с физическими симптомами (бессонница, боль). Дай совет по расслаблению, избегая клише про медитацию.\",\n" +
                "  \"nutrition_insight\": \"(3-4 предложения). Сопоставь граммы белков, жиров, углеводов и калорий (конкретные цифры приведи) с уровнем энергии или эмоциями. Найди конкретные дни 'заедания' или потери аппетита. Дай микро-совет по питанию в периоды спада.\",\n" +
                "  \"discipline_insight\": \"(3-4 предложения). Точные цифры выполнения привычек (например, 13 из 14). Сопоставь дни пропусков с объективными метриками (недосып, боль, стресс). Сними с пользователя чувство вины через физиологические причины.\",\n" +
                "  \"vitals_insight\": \"(3-4 предложения). Свяжи скачки давления и глюкозы (конкретные цифры приведи в пример) с событиями недели. ЗАПРЕЩЕНО писать 'важно добавить измерения'. Если данных нет, верни: 'Для точной аналитики не хватает регулярных замеров давления и глюкозы.'\"\n" +
                "}\n\n" +
                "ВХОДНЫЕ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ ЗА НЕДЕЛЮ (JSON):\n" + timelineJson + "\n\n" +
                "КРИТИЧЕСКОЕ ТРЕБОВАНИЕ: Выдай ТОЛЬКО чистый объект JSON. Не пиши заголовки типа '### Анализ', не используй символы #, не пиши никакой текст до и после JSON. Ответ должен начинаться с '{' и заканчиваться на '}'.";
        
        String aiResponseText = gigaChatService.generateInsight(prompt);

        
        return parseAiResponse(aiResponseText);
    }

    private WeeklySummaryResponse parseAiResponse(String aiResponse) {
        try {
            log.info("Начало парсинга ответа ИИ...");

            
            int firstBrace = aiResponse.indexOf('{');
            int lastBrace = aiResponse.lastIndexOf('}');

            if (firstBrace == -1 || lastBrace == -1) {
                log.error("ИИ прислал текст без JSON. Ответ: {}", aiResponse);
                return getFallbackResponse("Нейросеть подготовила ответ в свободном стиле. Попробуйте обновить отчет.");
            }

            
            String jsonPart = aiResponse.substring(firstBrace, lastBrace + 1);

            
            String cleanJson = jsonPart.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            return objectMapper.readValue(cleanJson, WeeklySummaryResponse.class);

        } catch (Exception e) {
            log.error("Ошибка десериализации. Оригинальный ответ ИИ: {}", aiResponse, e);
            return getFallbackResponse("Произошла ошибка при структурировании данных. Попробуйте еще раз.");
        }
    }

    
    private WeeklySummaryResponse getFallbackResponse(String summary) {
        return WeeklySummaryResponse.builder()
                .wellnessSummary(summary)
                .activityInsight("Данные временно недоступны")
                .recoveryMindInsight("-")
                .nutritionInsight("-")
                .disciplineInsight("-")
                .vitalsInsight("-")
                .build();
    }
}