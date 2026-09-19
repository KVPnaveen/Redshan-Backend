package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.ActivityLogDto;
import com.redshanflora.redshanflora_backend.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/live-activity")
    public ResponseEntity<List<ActivityLogDto>> getLiveActivities() {
        return ResponseEntity.ok(activityLogService.getRecentActivities());
    }
}
