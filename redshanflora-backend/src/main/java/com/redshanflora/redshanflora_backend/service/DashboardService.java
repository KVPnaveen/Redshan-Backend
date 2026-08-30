package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.DashboardStatsDTO;
import com.redshanflora.redshanflora_backend.dto.LowStockProductDTO;
import com.redshanflora.redshanflora_backend.dto.OrderMonthlyStatsDTO;
import com.redshanflora.redshanflora_backend.dto.RecentOrderDTO;

import java.util.List;

public interface DashboardService {

    DashboardStatsDTO getDashboardStats();

    List<OrderMonthlyStatsDTO> getMonthlyOrderStats();

    List<RecentOrderDTO> getRecentOrders();

    List<LowStockProductDTO> getLowStockProducts();




}
