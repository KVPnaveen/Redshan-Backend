package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.ActivityLogDto;
import com.redshanflora.redshanflora_backend.entity.ActivityLog;
import com.redshanflora.redshanflora_backend.repository.ActivityLogRepository;
import com.redshanflora.redshanflora_backend.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public ActivityLog logActivity(String activityType, String title, String description, String performedBy, String userRole) {
        log.info("Logging live activity: type={}, title={}, performedBy={}", activityType, title, performedBy);

        ActivityLog activityLog = ActivityLog.builder()
                .activityType(activityType != null ? activityType.toUpperCase() : "GENERAL")
                .title(title)
                .description(description)
                .performedBy(performedBy != null ? performedBy : "System Admin")
                .userRole(userRole != null ? userRole.toUpperCase() : "ADMIN")
                .timestamp(Instant.now())
                .build();

        return activityLogRepository.save(activityLog);
    }

    @Override
    @Transactional
    public List<ActivityLogDto> getRecentActivities() {
        List<ActivityLog> logs = activityLogRepository.findTop30ByOrderByTimestampDesc();

        if (logs.isEmpty()) {
            seedInitialActivities();
            logs = activityLogRepository.findTop30ByOrderByTimestampDesc();
        }

        return logs.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void seedInitialActivities() {
        log.info("Seeding initial live activity logs...");
        Instant now = Instant.now();

        List<ActivityLog> seedList = new ArrayList<>();

        seedList.add(ActivityLog.builder()
                .activityType("USER_ADDED")
                .title("Employee Added")
                .description("Nicolas Roche joined the Logistics team registered by Admin.")
                .performedBy("Claire (Owner Admin)")
                .userRole("ADMIN")
                .timestamp(now.minus(Duration.ofMinutes(2)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("PRODUCT_UPDATED")
                .title("Product Updated")
                .description("'Silk Velvet Orchid' inventory updated by Manager.")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofMinutes(45)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("TASK_ASSIGNED")
                .title("Employee Assigned to Work")
                .description("Manager assigned Order #RS-96 to Employee Kamal.")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofHours(1)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("ORDER_COMPLETED")
                .title("Order Completed")
                .description("Order #RS-2091 completed and dispatched to courier.")
                .performedBy("Logistics System")
                .userRole("ADMIN")
                .timestamp(now.minus(Duration.ofHours(2)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("PRODUCT_ADDED")
                .title("New Product Added")
                .description("Manager added 'Royal Peony Crown' to catalog.")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofHours(5)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("USER_ADDED")
                .title("Manager Added")
                .description("Sara Perera registered as Senior Manager.")
                .performedBy("Claire (Owner Admin)")
                .userRole("ADMIN")
                .timestamp(now.minus(Duration.ofHours(8)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("TASK_ASSIGNED")
                .title("Employee Assigned to Work")
                .description("Manager assigned Order #RS-95 to Employee Nimal.")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofHours(12)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("ORDER_COMPLETED")
                .title("Order Completed")
                .description("Order #RS-1088 marked completed.")
                .performedBy("Production System")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofDays(1)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("PRODUCT_UPDATED")
                .title("Product Stock Updated")
                .description("'Pink Roses Bouquet' stock updated (+20).")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofDays(1).plus(Duration.ofHours(3))))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("USER_ADDED")
                .title("Employee Added")
                .description("Kamal Perera joined the Production team.")
                .performedBy("Claire (Owner Admin)")
                .userRole("ADMIN")
                .timestamp(now.minus(Duration.ofDays(2)))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("PRODUCT_ADDED")
                .title("New Product Added")
                .description("Manager added 'Burgundy Bloom Halo' to catalog.")
                .performedBy("Sara Perera")
                .userRole("MANAGER")
                .timestamp(now.minus(Duration.ofDays(2).plus(Duration.ofHours(4))))
                .build());

        seedList.add(ActivityLog.builder()
                .activityType("ORDER_COMPLETED")
                .title("Order Completed")
                .description("Order #RS-1075 delivered successfully.")
                .performedBy("Logistics System")
                .userRole("ADMIN")
                .timestamp(now.minus(Duration.ofDays(3)))
                .build());

        activityLogRepository.saveAll(seedList);
    }

    private ActivityLogDto mapToDto(ActivityLog entity) {
        String type;
        String actType = entity.getActivityType() != null ? entity.getActivityType().toUpperCase() : "";

        if (actType.contains("USER") || actType.contains("EMPLOYEE") || actType.contains("MANAGER")) {
            type = "employee";
        } else if (actType.contains("PRODUCT")) {
            type = "product";
        } else if (actType.contains("ORDER")) {
            type = "order";
        } else if (actType.contains("TASK")) {
            type = "task";
        } else {
            type = "general";
        }

        return ActivityLogDto.builder()
                .id(entity.getId())
                .type(type)
                .activityType(entity.getActivityType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .performedBy(entity.getPerformedBy())
                .userRole(entity.getUserRole())
                .timestamp(entity.getTimestamp())
                .time(formatRelativeTime(entity.getTimestamp()))
                .build();
    }

    private String formatRelativeTime(Instant timestamp) {
        if (timestamp == null) return "Just now";
        Duration duration = Duration.between(timestamp, Instant.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Just now";
        } else if (seconds < 3600) {
            long mins = seconds / 60;
            return mins + (mins == 1 ? " min ago" : " mins ago");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else {
            long days = seconds / 86400;
            return days + (days == 1 ? " day ago" : " days ago");
        }
    }
}
