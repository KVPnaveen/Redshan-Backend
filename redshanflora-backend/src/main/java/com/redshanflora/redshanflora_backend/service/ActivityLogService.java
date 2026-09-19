package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.ActivityLogDto;
import com.redshanflora.redshanflora_backend.entity.ActivityLog;

import java.util.List;

public interface ActivityLogService {
    ActivityLog logActivity(String activityType, String title, String description, String performedBy, String userRole);
    List<ActivityLogDto> getRecentActivities();
}
