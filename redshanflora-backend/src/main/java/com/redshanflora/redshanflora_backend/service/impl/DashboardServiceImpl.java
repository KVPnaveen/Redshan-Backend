package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.DashboardStatsDTO;
import com.redshanflora.redshanflora_backend.dto.LowStockProductDTO;
import com.redshanflora.redshanflora_backend.dto.OrderMonthlyStatsDTO;
import com.redshanflora.redshanflora_backend.dto.RecentOrderDTO;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;

    public DashboardServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            EmployeeRepository employeeRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {

        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalEmployees = employeeRepository.count();

        return new DashboardStatsDTO(
                totalOrders,
                totalProducts,
                totalEmployees
        );
    }

    @Override
    public List<OrderMonthlyStatsDTO> getMonthlyOrderStats() {

        List<OrderMonthlyStatsDTO> monthlyStats = new ArrayList<>();

        // Sri Lanka time zone
        ZoneId zoneId = ZoneId.of("Asia/Colombo");

        // Current date
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // January -> current month
        for (int month = 1; month <= currentMonth; month++) {

            ZonedDateTime startOfMonth = ZonedDateTime.of(
                    currentYear,
                    month,
                    1,
                    0,
                    0,
                    0,
                    0,
                    zoneId
            );

            ZonedDateTime startOfNextMonth;

            if (month == 12) {

                startOfNextMonth = ZonedDateTime.of(
                        currentYear + 1,
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        zoneId
                );

            } else {

                startOfNextMonth = ZonedDateTime.of(
                        currentYear,
                        month + 1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        zoneId
                );
            }
            Instant startDate = startOfMonth.toInstant();
            Instant endDate = startOfNextMonth.toInstant();

            long orderCount =
                    orderRepository.countByOrderDateGreaterThanEqualAndOrderDateLessThan(
                            startDate,
                            endDate
                    );

            String monthName = startOfMonth
                    .getMonth()
                    .getDisplayName(
                            TextStyle.SHORT,
                            Locale.ENGLISH
                    );

            monthlyStats.add(
                    new OrderMonthlyStatsDTO(
                            month,
                            monthName,
                            orderCount
                    )
            );
        }

        return monthlyStats;

    }

    @Override
    public List<RecentOrderDTO> getRecentOrders() {

        List<Order> orders =
                orderRepository.findTop5ByOrderByOrderDateDesc();

        return orders.stream()
                .map(order -> new RecentOrderDTO(
                        order.getId(),
                        order.getCustomer()
                                .getUser()
                                .getName(),
                        order.getTotalAmount()
                ))
                .toList();
    }

    @Override
    public List<LowStockProductDTO> getLowStockProducts() {

        List<Product> products =
                productRepository
                        .findByStockQuantityLessThanOrderByStockQuantityAsc(10);

        return products.stream()
                .map(product -> new LowStockProductDTO(
                        product.getId(),
                        product.getProductName(),
                        product.getStockQuantity(),
                        product.getPrice()
                ))
                .toList();
    }
}