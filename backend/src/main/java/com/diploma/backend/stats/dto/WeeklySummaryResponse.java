package com.diploma.backend.stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySummaryResponse {
    @JsonProperty("wellness_summary")
    private String wellnessSummary;

    @JsonProperty("activity_insight")
    private String activityInsight;

    @JsonProperty("recovery_mind_insight")
    private String recoveryMindInsight;

    @JsonProperty("nutrition_insight")
    private String nutritionInsight;

    @JsonProperty("discipline_insight")
    private String disciplineInsight;

    @JsonProperty("vitals_insight")
    private String vitalsInsight;
}