package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(defaultValue = "last30days") String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Received request for admin reports dashboard, period: {}, startDate: {}, endDate: {}", period, startDate, endDate);
        Map<String, Object> data = adminReportService.getDashboardData(period, startDate, endDate);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<List<Map<String, Object>>> getMonthlySalesOverview() {
        log.info("Received request for 6-month sales overview");
        List<Map<String, Object>> data = adminReportService.getMonthlySalesOverview();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam(defaultValue = "last30days") String period) {
        log.info("Received request to export PDF report for period: {}", period);
        byte[] pdfBytes = adminReportService.generatePdfReport(period);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"RedShan360-Analytics-Report.pdf\"")
                .body(pdfBytes);
    }
}
