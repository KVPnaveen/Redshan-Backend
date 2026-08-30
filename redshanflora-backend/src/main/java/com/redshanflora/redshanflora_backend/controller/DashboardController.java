package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.LowStockProductDTO;
import com.redshanflora.redshanflora_backend.dto.OrderMonthlyStatsDTO;
import com.redshanflora.redshanflora_backend.dto.RecentOrderDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.redshanflora.redshanflora_backend.dto.DashboardStatsDTO;
import com.redshanflora.redshanflora_backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {

        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/order-analytics")
    public ResponseEntity<List<OrderMonthlyStatsDTO>> getMonthlyOrderStats() {

        return ResponseEntity.ok(
                dashboardService.getMonthlyOrderStats()
        );
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders() {

        return ResponseEntity.ok(
                dashboardService.getRecentOrders()
        );
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts() {

        return ResponseEntity.ok(
                dashboardService.getLowStockProducts()
        );
    }
}
