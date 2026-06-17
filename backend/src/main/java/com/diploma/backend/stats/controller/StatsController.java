package com.diploma.backend.stats.controller;

import com.diploma.backend.stats.dto.WeeklySummaryResponse;
import com.diploma.backend.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/weekly")
    public ResponseEntity<WeeklySummaryResponse> getWeeklySummary(
            Authentication authentication, 
            @RequestParam(required = false) String dateStr 
    ) {
        
        String userId = authentication.getName();

        
        String endDate = (dateStr != null && !dateStr.isEmpty()) ? dateStr : LocalDate.now().toString();

        WeeklySummaryResponse response = statsService.getWeeklySummary(userId, endDate);

        return ResponseEntity.ok(response);
    }
}