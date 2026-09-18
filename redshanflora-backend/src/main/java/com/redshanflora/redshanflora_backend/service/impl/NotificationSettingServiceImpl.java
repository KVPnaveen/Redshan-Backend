package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.notification.NotificationDto;
import com.redshanflora.redshanflora_backend.dto.notification.NotificationSettingDto;
import com.redshanflora.redshanflora_backend.entity.Notification;
import com.redshanflora.redshanflora_backend.entity.NotificationSetting;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.repository.NotificationRepository;
import com.redshanflora.redshanflora_backend.repository.NotificationSettingRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingRepository settingRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public NotificationSettingDto getNotificationSettings() {
        User user = getAuthenticatedUser();
        NotificationSetting setting = settingRepository.findByUser(user)
                .orElseGet(() -> {
                    log.info("Creating default NotificationSetting for user: {}", user.getEmail());
                    NotificationSetting newSetting = NotificationSetting.builder()
                            .user(user)
                            .emailNotifications(true)
                            .newOrderAlerts(true)
                            .lowStockAlerts(true)
                            .monthlyReportEmails(true)
                            .lowStockThreshold(5)
                            .build();
                    return settingRepository.save(newSetting);
                });

        return mapToDto(setting);
    }

    @Override
    @Transactional
    public NotificationSettingDto updateNotificationSettings(NotificationSettingDto dto) {
        User user = getAuthenticatedUser();
        NotificationSetting setting = settingRepository.findByUser(user)
                .orElseGet(() -> NotificationSetting.builder().user(user).build());

        if (dto.getEmailNotifications() != null) {
            setting.setEmailNotifications(dto.getEmailNotifications());
        }
        if (dto.getNewOrderAlerts() != null) {
            setting.setNewOrderAlerts(dto.getNewOrderAlerts());
        }
        if (dto.getLowStockAlerts() != null) {
            setting.setLowStockAlerts(dto.getLowStockAlerts());
        }
        if (dto.getMonthlyReportEmails() != null) {
            setting.setMonthlyReportEmails(dto.getMonthlyReportEmails());
        }
        if (dto.getLowStockThreshold() != null) {
            setting.setLowStockThreshold(dto.getLowStockThreshold());
        }

        NotificationSetting saved = settingRepository.save(setting);
        log.info("Updated notification settings for user {}: email={}, newOrder={}, lowStock={}, monthlyReport={}, threshold={}",
                user.getEmail(), saved.getEmailNotifications(), saved.getNewOrderAlerts(),
                saved.getLowStockAlerts(), saved.getMonthlyReportEmails(), saved.getLowStockThreshold());
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications() {
        User user = getAuthenticatedUser();
        // Strictly fetch true rows from PostgreSQL database public.notification table
        List<Notification> list = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return list.stream()
                .map(this::mapToNotificationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        User user = getAuthenticatedUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized attempt to modify notification ID: " + notificationId);
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead() {
        User user = getAuthenticatedUser();
        List<Notification> unreadList = notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(n -> Boolean.FALSE.equals(n.getIsRead()))
                .collect(Collectors.toList());
        
        unreadList.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadList);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount() {
        User user = getAuthenticatedUser();
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void createNotificationForAdmins(String type, String title, String message, Long referenceId) {
        List<User> adminUsers = userRepository.findByRoleIn(List.of(Role.ADMIN, Role.MANAGER));
        if (adminUsers.isEmpty()) {
            adminUsers = userRepository.findAll();
        }

        Instant now = Instant.now();
        for (User admin : adminUsers) {
            NotificationSetting setting = settingRepository.findByUser(admin).orElse(null);
            boolean allowNotification = true;

            if (setting != null) {
                if ("NEW_ORDER".equals(type) && Boolean.FALSE.equals(setting.getNewOrderAlerts())) {
                    allowNotification = false;
                } else if ("LOW_STOCK".equals(type) && Boolean.FALSE.equals(setting.getLowStockAlerts())) {
                    allowNotification = false;
                } else if (("REPORT_DOWNLOADED".equals(type) || "REPORT".equals(type)) && Boolean.FALSE.equals(setting.getMonthlyReportEmails())) {
                    allowNotification = false;
                } else if (("EMAIL_COMMUNICATION".equals(type) || "EMAIL_RECEIVED".equals(type)) && Boolean.FALSE.equals(setting.getEmailNotifications())) {
                    allowNotification = false;
                }
            }

            if (allowNotification) {
                Notification notification = Notification.builder()
                        .user(admin)
                        .type(type)
                        .title(title)
                        .message(message)
                        .referenceId(referenceId)
                        .isRead(false)
                        .createdAt(now)
                        .build();
                notificationRepository.save(notification);
                log.info("Saved true notification in database for admin {}: type={}, title={}", admin.getEmail(), type, title);
            }
        }
    }

    @Override
    @Transactional
    public void createNotificationForUser(User user, String type, String title, String message, Long referenceId) {
        if (user == null) return;
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .createdAt(Instant.now())
                .build();
        notificationRepository.save(notification);
        log.info("Saved true notification in database for user {}: type={}, title={}", user.getEmail(), type, title);
    }

    @Override
    @Transactional
    public void checkAndNotifyLowStock(Product product) {
        if (product == null || product.getId() == null) return;

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        List<User> adminUsers = userRepository.findByRoleIn(List.of(Role.ADMIN, Role.MANAGER));
        if (adminUsers.isEmpty()) {
            adminUsers = userRepository.findAll();
        }

        int threshold = 5;
        if (!adminUsers.isEmpty()) {
            NotificationSetting setting = settingRepository.findByUser(adminUsers.get(0)).orElse(null);
            if (setting != null && setting.getLowStockThreshold() != null) {
                threshold = setting.getLowStockThreshold();
            }
        }

        if (currentStock <= threshold) {
            String title = currentStock == 0 ? "Out of Stock Alert" : "Low Stock Alert";
            String message = currentStock == 0
                    ? "Product '" + product.getProductName() + "' (ID: " + product.getId() + ") is completely out of stock!"
                    : "Product '" + product.getProductName() + "' (ID: " + product.getId() + ") stock is low. Remaining: " + currentStock + " items.";

            createNotificationForAdmins("LOW_STOCK", title, message, product.getId());
        }
    }


    private NotificationSettingDto mapToDto(NotificationSetting setting) {
        return NotificationSettingDto.builder()
                .emailNotifications(setting.getEmailNotifications())
                .newOrderAlerts(setting.getNewOrderAlerts())
                .lowStockAlerts(setting.getLowStockAlerts())
                .monthlyReportEmails(setting.getMonthlyReportEmails())
                .lowStockThreshold(setting.getLowStockThreshold())
                .build();
    }

    private NotificationDto mapToNotificationDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
