package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.notification.NotificationSettingDto;
import com.redshanflora.redshanflora_backend.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminNotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    public ResponseEntity<NotificationSettingDto> getNotificationSettings() {
        log.info("Received GET request for admin notification settings");
        NotificationSettingDto settings = notificationSettingService.getNotificationSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateNotificationSettings(@RequestBody NotificationSettingDto dto) {
        log.info("Received PUT request to update admin notification settings");
        NotificationSettingDto updated = notificationSettingService.updateNotificationSettings(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Notification preferences saved successfully.");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }
}
