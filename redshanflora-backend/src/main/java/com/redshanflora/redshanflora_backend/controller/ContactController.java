package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.contact.ContactDto;
import com.redshanflora.redshanflora_backend.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ContactController {

    private final NotificationSettingService notificationSettingService;

    @PostMapping
    public ResponseEntity<Map<String, String>> handleContactForm(@RequestBody ContactDto dto) {
        log.info("Received contact form submission from email: {}", dto.getEmail());

        String senderName = dto.getFullName() != null && !dto.getFullName().isBlank() ? dto.getFullName() : "Guest User";
        String subjectText = dto.getSubject() != null && !dto.getSubject().isBlank() ? dto.getSubject() : "General Inquiry";
        String bodyText = dto.getMessage() != null && !dto.getMessage().isBlank() ? dto.getMessage() : "No message body provided";

        try {
            notificationSettingService.createNotificationForAdmins(
                "EMAIL_COMMUNICATION",
                "New Email / Inquiry Received",
                "Message from " + senderName + " (" + (dto.getEmail() != null ? dto.getEmail() : "N/A") + "): [" + subjectText + "] " + bodyText,
                null
            );
        } catch (Exception e) {
            log.warn("Could not create EMAIL_COMMUNICATION notification: {}", e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Your message has been received. Thank you for contacting RedshanFlora!"
        ));
    }
}
