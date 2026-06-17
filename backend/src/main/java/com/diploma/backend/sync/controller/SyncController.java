package com.diploma.backend.sync.controller;

import com.diploma.backend.sync.dto.SyncDataDto;
import com.diploma.backend.sync.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @GetMapping("/pull")
    public ResponseEntity<SyncDataDto> pull(
            @RequestParam(defaultValue = "0") Long lastSyncTimestamp
    ) {
        // Достаем ID юзера из SecurityContext (его туда положил наш JwtFilter)
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SyncDataDto data = syncService.pullData(userId, lastSyncTimestamp);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/push")
    public ResponseEntity<?> push( // <-- ЗАМЕНИЛА String на ?
                                   @RequestBody SyncDataDto syncData
    ) {
        // Достаем ID юзера из SecurityContext
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        syncService.pushData(userId, syncData);

        // Теперь это вернет корректный JSON: {"message": "Sync successful"}
        return ResponseEntity.ok(Map.of("message", "Sync successful"));
    }
}