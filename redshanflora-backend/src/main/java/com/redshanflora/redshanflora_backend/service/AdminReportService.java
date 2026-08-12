package com.redshanflora.redshanflora_backend.service;

import java.util.List;
import java.util.Map;

public interface AdminReportService {

    Map<String, Object> getDashboardData(String period, String startDate, String endDate);

    List<Map<String, Object>> getMonthlySalesOverview();

    byte[] generatePdfReport(String period);
}
