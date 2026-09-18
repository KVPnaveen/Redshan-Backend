package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.notification.NotificationDto;
import com.redshanflora.redshanflora_backend.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminNotificationController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications() {
        log.info("Received GET request for admin user notifications");
        List<NotificationDto> notifications = notificationSettingService.getUserNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        long count = notificationSettingService.getUnreadNotificationCount();
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        log.info("Received PUT request to mark notification ID={} as read", id);
        notificationSettingService.markNotificationAsRead(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Notification marked as read.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        log.info("Received PUT request to mark all notifications as read");
        notificationSettingService.markAllNotificationsAsRead();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All notifications marked as read.");
        return ResponseEntity.ok(response);
    }
}
