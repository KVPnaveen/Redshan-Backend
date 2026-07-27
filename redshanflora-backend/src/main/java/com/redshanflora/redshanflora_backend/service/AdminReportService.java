package com.redshanflora.redshanflora_backend.service;

import java.util.Map;

public interface AdminReportService {
    Map<String, Object> getDashboardData(String period, String startDate, String endDate);
}
