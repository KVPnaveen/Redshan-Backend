package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.notification.NotificationDto;
import com.redshanflora.redshanflora_backend.dto.notification.NotificationSettingDto;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;

import java.util.List;

public interface NotificationSettingService {
    NotificationSettingDto getNotificationSettings();
    NotificationSettingDto updateNotificationSettings(NotificationSettingDto dto);

    List<NotificationDto> getUserNotifications();
    void markNotificationAsRead(Long notificationId);
    void markAllNotificationsAsRead();
    long getUnreadNotificationCount();

    void createNotificationForAdmins(String type, String title, String message, Long referenceId);
    void createNotificationForUser(User user, String type, String title, String message, Long referenceId);
    void checkAndNotifyLowStock(Product product);
}

