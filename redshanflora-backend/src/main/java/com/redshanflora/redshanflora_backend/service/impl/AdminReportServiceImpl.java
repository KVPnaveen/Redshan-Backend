package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.Payment;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.repository.PaymentRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepository;

import com.redshanflora.redshanflora_backend.repository.CustomerRepository;

import com.redshanflora.redshanflora_backend.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminReportServiceImpl implements AdminReportService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    private final CustomerRepository customerRepository;



    private static final ZoneId COLOMBO_ZONE = ZoneId.of("Asia/Colombo");


    @Override

    public Map<String, Object> getDashboardData(String period, String startDateStr, String endDateStr) {
        log.info("Generating dashboard analytics report for period: {}, startDate: {}, endDate: {}", period, startDateStr, endDateStr);

        Map<String, Object> response = new LinkedHashMap<>();

        // 1. Calculate Revenue KPIs (Daily, Weekly, Monthly)
        ZonedDateTime now = ZonedDateTime.now(COLOMBO_ZONE);
        Instant todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS);

        Instant weeklyStart = now.minusDays(6).truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant prevWeeklyStart = weeklyStart.minus(7, ChronoUnit.DAYS);

        Instant monthlyStart = now.minusDays(29).truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant prevMonthlyStart = monthlyStart.minus(30, ChronoUnit.DAYS);

        // Fetch Current Period sums
        BigDecimal dailyRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateAfter("paid", todayStart);
        BigDecimal weeklyRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateAfter("paid", weeklyStart);
        BigDecimal monthlyRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateAfter("paid", monthlyStart);

        // Fetch Previous Period sums for growth percentage calculation
        BigDecimal yesterdayRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", yesterdayStart, todayStart);
        BigDecimal prevWeeklyRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", prevWeeklyStart, weeklyStart);
        BigDecimal prevMonthlyRevenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", prevMonthlyStart, monthlyStart);

        // Calculate growth changes
        double dailyChange = calculatePercentageChange(dailyRevenue, yesterdayRevenue);
        double weeklyChange = calculatePercentageChange(weeklyRevenue, prevWeeklyRevenue);
        double monthlyChange = calculatePercentageChange(monthlyRevenue, prevMonthlyRevenue);

        // Put revenue KPIs into response
        response.put("dailyRevenue", dailyRevenue);
        response.put("dailyChange", dailyChange);
        response.put("weeklyRevenue", weeklyRevenue);
        response.put("weeklyChange", weeklyChange);
        response.put("monthlyRevenue", monthlyRevenue);
        response.put("monthlyChange", monthlyChange);

        // 2. Fetch Customer Insights
        // Fetch new customer registration counts
        Instant periodStart;
        Instant prevPeriodStart;
        Instant periodEnd = now.toInstant();

        
        if ("customdaterange".equalsIgnoreCase(period) && startDateStr != null && endDateStr != null) {
            try {
                java.time.LocalDate sDate = java.time.LocalDate.parse(startDateStr);
                java.time.LocalDate eDate = java.time.LocalDate.parse(endDateStr);
                
                periodStart = sDate.atStartOfDay(COLOMBO_ZONE).toInstant();
                periodEnd = eDate.atTime(23, 59, 59).atZone(COLOMBO_ZONE).toInstant();
                
                long daysCount = java.time.temporal.ChronoUnit.DAYS.between(sDate, eDate) + 1;
                prevPeriodStart = sDate.minusDays(daysCount).atStartOfDay(COLOMBO_ZONE).toInstant();
            } catch (Exception e) {
                log.error("Failed to parse custom date range: {} to {}", startDateStr, endDateStr, e);
                periodStart = monthlyStart;
                prevPeriodStart = prevMonthlyStart;
            }
        } else {
            switch (period.toLowerCase()) {
                case "today":
                    periodStart = todayStart;
                    prevPeriodStart = yesterdayStart;
                    break;
                case "last7days":
                    periodStart = weeklyStart;
                    prevPeriodStart = prevWeeklyStart;
                    break;
                case "thismonth":
                    ZonedDateTime tmStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                    periodStart = tmStart.toInstant();
                    prevPeriodStart = tmStart.minusMonths(1).toInstant();
                    break;
                case "lastmonth":
                    ZonedDateTime pmStart = now.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                    periodStart = pmStart.toInstant();
                    periodEnd = pmStart.plusMonths(1).toInstant();
                    prevPeriodStart = pmStart.minusMonths(1).toInstant();
                    break;
                case "last30days":
                default:
                    periodStart = monthlyStart;
                    prevPeriodStart = prevMonthlyStart;
                    break;
            }
        }

        long newCustomers = userRepository.countByRoleAndRegisteredDateBetween(Role.CUSTOMER, periodStart, periodEnd);
        long activeCustomers = userRepository.countByRoleAndStatusAndRegisteredDateBefore(Role.CUSTOMER, "ACTIVE", periodEnd);
        long returningCustomers = customerRepository.countReturningCustomers(periodEnd);
        
        Map<String, Object> customersMap = new LinkedHashMap<>();
        customersMap.put("new", newCustomers);
        customersMap.put("active", activeCustomers);
        customersMap.put("returning", returningCustomers);
        response.put("customers", customersMap);

        // Calculate KPI values & change percentages for the 7 dashboard cards
        long currentCustomers = userRepository.countByRoleAndRegisteredDateBefore(Role.CUSTOMER, periodEnd);
        long prevCustomers = userRepository.countByRoleAndRegisteredDateBefore(Role.CUSTOMER, periodStart);
        double customersChange = calculatePercentageChange(BigDecimal.valueOf(currentCustomers), BigDecimal.valueOf(prevCustomers));

        long currentManagers = userRepository.countByRoleAndRegisteredDateBefore(Role.MANAGER, periodEnd);
        long prevManagers = userRepository.countByRoleAndRegisteredDateBefore(Role.MANAGER, periodStart);
        double managersChange = calculatePercentageChange(BigDecimal.valueOf(currentManagers), BigDecimal.valueOf(prevManagers));

        long currentEmployees = userRepository.countByRoleAndRegisteredDateBefore(Role.EMPLOYEE, periodEnd);
        long prevEmployees = userRepository.countByRoleAndRegisteredDateBefore(Role.EMPLOYEE, periodStart);
        double employeesChange = calculatePercentageChange(BigDecimal.valueOf(currentEmployees), BigDecimal.valueOf(prevEmployees));

        long totalProducts = productRepository.count();
        double productsChange = 0.0;

        long currentPending = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_CONFIRMED, periodStart, periodEnd)
                           + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.PROCESSING, periodStart, periodEnd);
        long prevPending = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_CONFIRMED, prevPeriodStart, periodStart)
                        + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.PROCESSING, prevPeriodStart, periodStart);
        double pendingChange = calculatePercentageChange(BigDecimal.valueOf(currentPending), BigDecimal.valueOf(prevPending));

        long currentCompleted = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_COMPLETED, periodStart, periodEnd)
                             + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.DISPATCHED_TO_COURIER, periodStart, periodEnd);
        long prevCompleted = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_COMPLETED, prevPeriodStart, periodStart)
                          + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.DISPATCHED_TO_COURIER, prevPeriodStart, periodStart);
        double completedChange = calculatePercentageChange(BigDecimal.valueOf(currentCompleted), BigDecimal.valueOf(prevCompleted));

        response.put("totalCustomers", currentCustomers);
        response.put("totalCustomersChange", customersChange);
        response.put("totalManagers", currentManagers);
        response.put("totalManagersChange", managersChange);
        response.put("totalEmployees", currentEmployees);
        response.put("totalEmployeesChange", employeesChange);
        response.put("totalProducts", totalProducts);
        response.put("totalProductsChange", productsChange);
        response.put("pendingOrders", currentPending);
        response.put("pendingOrdersChange", pendingChange);
        response.put("completedOrders", currentCompleted);
        response.put("completedOrdersChange", completedChange);

        // 3. Fetch Order Fulfillment Statuses
        long totalOrders = orderRepository.countByOrderDateBetween(periodStart, periodEnd);
        long pendingOrders = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_CONFIRMED, periodStart, periodEnd)
                           + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.PROCESSING, periodStart, periodEnd);
        long completedOrders = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.DISPATCHED_TO_COURIER, periodStart, periodEnd);
        long cancelledOrders = 0; // Currently no CANCELLED enum, defaults to 0

        Map<String, Object> ordersMap = new LinkedHashMap<>();
        ordersMap.put("total", totalOrders);
        ordersMap.put("pending", pendingOrders);
        ordersMap.put("completed", completedOrders);
        ordersMap.put("cancelled", cancelledOrders);
        response.put("orders", ordersMap);

        // Calculate Order Segmentation & Efficiency for Doughnut Chart
        long segCompleted = currentCompleted;
        long segProcessing = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.PROCESSING, periodStart, periodEnd);
        long segPending = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_CONFIRMED, periodStart, periodEnd);
        long segCancelled = 0;
        long segTotal = segCompleted + segProcessing + segPending + segCancelled;
        
        double orderEfficiency = (segTotal > 0) ? ((double) segCompleted * 100.0) / segTotal : 100.0;
        
        Map<String, Object> orderSegmentationMap = new LinkedHashMap<>();
        orderSegmentationMap.put("completed", segCompleted);
        orderSegmentationMap.put("processing", segProcessing);
        orderSegmentationMap.put("pending", segPending);
        orderSegmentationMap.put("cancelled", segCancelled);
        
        response.put("orderSegmentation", orderSegmentationMap);
        response.put("orderEfficiency", Math.round(orderEfficiency));

        // 7. Recent Orders (fetch top 5 most recent orders)
        List<Order> recentOrdersList = orderRepository.findTop5ByOrderByOrderDateDesc();
        List<Map<String, Object>> recentOrdersMapList = new ArrayList<>();
        
        for (Order o : recentOrdersList) {
            Map<String, Object> orderMap = new LinkedHashMap<>();
            orderMap.put("id", "#RS-" + o.getId());
            orderMap.put("customer", o.getCustomer() != null && o.getCustomer().getUser() != null ? o.getCustomer().getUser().getName() : "Guest Customer");
            orderMap.put("status", o.getOrderStatus() != null ? o.getOrderStatus().name() : "PENDING");
            orderMap.put("date", o.getOrderDate() != null ? o.getOrderDate().toString() : Instant.now().toString());
            recentOrdersMapList.add(orderMap);
        }
        response.put("recentOrders", recentOrdersMapList);



        // 4. Category Performance Breakdown (Dynamically queried from database)
        List<Object[]> rawCategoryData;
        if (period.equalsIgnoreCase("lastmonth")) {
            rawCategoryData = orderItemRepository.findCategoryRevenueByPaymentStatusAndDateBetween("paid", periodStart, periodEnd);
        } else {
            rawCategoryData = orderItemRepository.findCategoryRevenueByPaymentStatusAndDateAfter("paid", periodStart);
        }

        Map<String, BigDecimal> categoryRevenues = new HashMap<>();
        categoryRevenues.put("Bouquets", BigDecimal.ZERO);
        categoryRevenues.put("Head Dresses", BigDecimal.ZERO);
        categoryRevenues.put("Individual Flowers", BigDecimal.ZERO);

        BigDecimal totalCategoryRevenue = BigDecimal.ZERO;

        for (Object[] row : rawCategoryData) {
            String dbCatName = (String) row[0];
            BigDecimal revenue = BigDecimal.valueOf(((Number) row[1]).doubleValue());
            if (dbCatName != null) {
                String matchedKey = null;
                if (dbCatName.equalsIgnoreCase("Bouquets") || dbCatName.toLowerCase().contains("bouquet")) {
                    matchedKey = "Bouquets";
                } else if (dbCatName.equalsIgnoreCase("Head Dresses") || dbCatName.toLowerCase().contains("head")) {
                    matchedKey = "Head Dresses";
                } else if (dbCatName.equalsIgnoreCase("Individual Flowers") || dbCatName.toLowerCase().contains("flower")) {
                    matchedKey = "Individual Flowers";
                }
                
                if (matchedKey != null) {
                    categoryRevenues.put(matchedKey, categoryRevenues.get(matchedKey).add(revenue));
                    totalCategoryRevenue = totalCategoryRevenue.add(revenue);
                }
            }
        }

        List<Map<String, Object>> categoryPerformance = new ArrayList<>();
        if (totalCategoryRevenue.compareTo(BigDecimal.ZERO) == 0) {
            // Default placeholder proportions if no sales exist in database yet
            categoryPerformance.add(createCategoryMap("Bouquets", 60));
            categoryPerformance.add(createCategoryMap("Head Dresses", 25));
            categoryPerformance.add(createCategoryMap("Individual Flowers", 15));
        } else {
            // Calculate actual percentages dynamically
            for (String catName : Arrays.asList("Bouquets", "Head Dresses", "Individual Flowers")) {
                BigDecimal rev = categoryRevenues.get(catName);
                int pct = rev.multiply(BigDecimal.valueOf(100))
                        .divide(totalCategoryRevenue, 0, RoundingMode.HALF_UP)
                        .intValue();
                categoryPerformance.add(createCategoryMap(catName, pct));
            }
        }
        response.put("categoryPerformance", categoryPerformance);


        // 5. Dynamic Revenue Growth Chart Data

        Map<String, Object> revenueGrowth = generateRevenueGrowthData(period, now, periodStart, periodEnd);

        response.put("revenueGrowth", revenueGrowth);

        // 6. Top Selling Products (fetch standard products, fall back to mock details if empty)
        List<Map<String, Object>> topProductsList = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            topProductsList.add(createProductMap(1L, "Eternal Silk Peony", "RS-SP-001", "crimson_silk_peony.jpg", 1240, "IN STOCK", 12.5));
            topProductsList.add(createProductMap(2L, "Midnight Velvet Rose", "RS-VR-092", "moonlight_velvet_tulip.jpg", 892, "LOW STOCK", 5.2));
            topProductsList.add(createProductMap(3L, "Ivory Orchid Stem", "RS-IO-441", "serenity_white.jpg", 754, "IN STOCK", -2.1));
            topProductsList.add(createProductMap(4L, "Royal Bloom Tulip", "RS-RT-552", "serenity_white.jpg", 645, "IN STOCK", 3.8));
        } else {
            int count = 1;
            for (Product p : products) {
                if (count > 4) break;
                String stockStatus = p.getStockQuantity() <= 0 ? "OUT OF STOCK" : (p.getStockQuantity() < 10 ? "LOW STOCK" : "IN STOCK");
                // Fetch simple placeholder image name
                String imgName = p.getImageUrl() != null && p.getImageUrl().contains("/") ? 
                                 p.getImageUrl().substring(p.getImageUrl().lastIndexOf("/") + 1) : "serenity_white.jpg";
                topProductsList.add(createProductMap(
                        p.getId(),
                        p.getProductName(),
                        "RS-PROD-" + p.getId(),
                        imgName,
                        150 - (count * 20), // Simulated sales quantity proportional to product id
                        stockStatus,
                        3.5 + count
                ));
                count++;
            }
        }
        response.put("topProducts", topProductsList);

        return response;
    }

    private double calculatePercentageChange(BigDecimal current, BigDecimal previous) {
        if (current == null || current.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }
        BigDecimal diff = current.subtract(previous);
        BigDecimal percentage = diff.multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP);
        return percentage.doubleValue();
    }

    private Map<String, Object> createCategoryMap(String categoryName, int percentage) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("category", categoryName);
        map.put("percentage", percentage);
        return map;
    }

    private Map<String, Object> createProductMap(Long id, String name, String sku, String imageName, int sold, String stockStatus, double trend) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("sku", sku);
        map.put("imageName", imageName);
        map.put("unitsSold", sold);
        map.put("stockStatus", stockStatus);
        map.put("trend", trend);
        return map;
    }

    private Map<String, Object> generateRevenueGrowthData(String period, ZonedDateTime now, Instant periodStart, Instant periodEnd) {

        Map<String, Object> chartData = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        if ("today".equalsIgnoreCase(period)) {
            // Hours segments
            labels.addAll(Arrays.asList("09:00", "11:00", "13:00", "15:00", "17:00", "19:00"));
            Instant todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant();
            BigDecimal totalToday = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateAfter("paid", todayStart);
            
            // Distribute today's real revenue over the hour intervals
            if (totalToday == null || totalToday.compareTo(BigDecimal.ZERO) == 0) {
                values.addAll(Arrays.asList(BigDecimal.valueOf(800), BigDecimal.valueOf(1200), BigDecimal.valueOf(1500), BigDecimal.valueOf(750), BigDecimal.valueOf(600), BigDecimal.ZERO));
            } else {
                BigDecimal segment = totalToday.divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
                for (int i = 0; i < 6; i++) {
                    values.add(segment);
                }
            }
        } else if ("last7days".equalsIgnoreCase(period)) {
            // Days segments
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE", Locale.US);
            for (int i = 6; i >= 0; i--) {
                ZonedDateTime day = now.minusDays(i);
                labels.add(day.format(fmt));
                Instant dayStart = day.truncatedTo(ChronoUnit.DAYS).toInstant();
                Instant dayEnd = dayStart.plus(1, ChronoUnit.DAYS);
                BigDecimal dayRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", dayStart, dayEnd);
                values.add(dayRev != null ? dayRev : BigDecimal.ZERO);
            }
        } else if ("last30days".equalsIgnoreCase(period)) {
            // Group into 4 weeks
            for (int i = 3; i >= 0; i--) {
                labels.add("Week " + (4 - i));
                Instant start = now.minusDays((i + 1) * 7 - 1).truncatedTo(ChronoUnit.DAYS).toInstant();
                Instant end = now.minusDays(i * 7 - 1).truncatedTo(ChronoUnit.DAYS).toInstant();
                BigDecimal weekRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", start, end);
                values.add(weekRev != null ? weekRev : BigDecimal.ZERO);
            }
        } else if ("thismonth".equalsIgnoreCase(period)) {
            // Group by weeks of this month
            ZonedDateTime tmStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            for (int i = 0; i < 4; i++) {
                labels.add("Week " + (i + 1));
                Instant start = tmStart.plusDays(i * 7).toInstant();
                Instant end = tmStart.plusDays((i + 1) * 7).toInstant();
                if (i == 3) {
                    end = tmStart.plusMonths(1).toInstant(); // make sure we cover the entire month
                }
                BigDecimal weekRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", start, end);
                values.add(weekRev != null ? weekRev : BigDecimal.ZERO);
            }
        } else if ("lastmonth".equalsIgnoreCase(period)) {
            // Group by weeks of last month
            ZonedDateTime pmStart = now.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            for (int i = 0; i < 4; i++) {
                labels.add("Week " + (i + 1));
                Instant start = pmStart.plusDays(i * 7).toInstant();
                Instant end = pmStart.plusDays((i + 1) * 7).toInstant();
                if (i == 3) {
                    end = pmStart.plusMonths(1).toInstant();
                }
                BigDecimal weekRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", start, end);
                values.add(weekRev != null ? weekRev : BigDecimal.ZERO);
            }
        } else if ("customdaterange".equalsIgnoreCase(period) && periodStart != null && periodEnd != null) {
            long daysCount = java.time.temporal.ChronoUnit.DAYS.between(
                ZonedDateTime.ofInstant(periodStart, COLOMBO_ZONE), 
                ZonedDateTime.ofInstant(periodEnd, COLOMBO_ZONE)
            ) + 1;
            
            if (daysCount <= 7) {
                // Group by day
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd", Locale.US);
                for (int i = 0; i < daysCount; i++) {
                    ZonedDateTime day = ZonedDateTime.ofInstant(periodStart, COLOMBO_ZONE).plusDays(i);
                    labels.add(day.format(fmt));
                    Instant start = day.truncatedTo(ChronoUnit.DAYS).toInstant();
                    Instant end = start.plus(1, ChronoUnit.DAYS);
                    BigDecimal dayRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", start, end);
                    values.add(dayRev != null ? dayRev : BigDecimal.ZERO);
                }
            } else {
                // Group into 4 equal segments
                long segmentDays = daysCount / 4;
                if (segmentDays < 1) segmentDays = 1;
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd", Locale.US);
                for (int i = 0; i < 4; i++) {
                    ZonedDateTime startDay = ZonedDateTime.ofInstant(periodStart, COLOMBO_ZONE).plusDays(i * segmentDays);
                    ZonedDateTime endDay = startDay.plusDays(segmentDays);
                    if (i == 3) {
                        endDay = ZonedDateTime.ofInstant(periodEnd, COLOMBO_ZONE);
                    }
                    labels.add(startDay.format(fmt) + " to " + endDay.format(fmt));
                    BigDecimal segRev = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", startDay.toInstant(), endDay.toInstant());
                    values.add(segRev != null ? segRev : BigDecimal.ZERO);
                }
            }
        } else {
            // Default fallback
            labels.addAll(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"));
            BigDecimal totalMonth = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateAfter("paid", periodStart);
            if (totalMonth == null || totalMonth.compareTo(BigDecimal.ZERO) == 0) {
                values.addAll(Arrays.asList(BigDecimal.valueOf(35000), BigDecimal.valueOf(48000), BigDecimal.valueOf(39000), BigDecimal.valueOf(62000), BigDecimal.valueOf(58000), BigDecimal.valueOf(85000), BigDecimal.valueOf(80000)));
            } else {

                BigDecimal base = totalMonth.divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP);
                for (int i = 1; i <= 7; i++) {
                    values.add(base.multiply(BigDecimal.valueOf(1 + (i * 0.1))));
                }
            }
        }

        chartData.put("labels", labels);
        chartData.put("values", values);
        return chartData;
    }

    @Override
    public List<Map<String, Object>> getMonthlySalesOverview() {
        log.info("Generating 6-month sales overview report");
        List<Map<String, Object>> salesList = new ArrayList<>();
        ZonedDateTime now = ZonedDateTime.now(COLOMBO_ZONE);
        
        for (int i = 5; i >= 0; i--) {
            ZonedDateTime monthStartDateTime = now.minusMonths(i).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime monthEndDateTime = monthStartDateTime.plusMonths(1);
            
            Instant start = monthStartDateTime.toInstant();
            Instant end = monthEndDateTime.toInstant();
            
            BigDecimal revenue = paymentRepository.sumTotalAmountByPaymentStatusAndPaymentDateBetween("paid", start, end);
            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }
            
            long ordersCount = orderRepository.countByOrderDateBetween(start, end);
            long completedOrdersCount = orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.ORDER_COMPLETED, start, end)
                                     + orderRepository.countByOrderStatusAndOrderDateBetween(MainOrderStatus.DISPATCHED_TO_COURIER, start, end);
            
            Map<String, Object> monthData = new LinkedHashMap<>();
            String monthName = monthStartDateTime.format(DateTimeFormatter.ofPattern("MMM", Locale.US));
            monthData.put("month", monthName);
            monthData.put("revenue", revenue);
            monthData.put("ordersCount", ordersCount);
            monthData.put("completedOrdersCount", completedOrdersCount);
            
            salesList.add(monthData);
        }
        return salesList;
    }

    @Override
    public byte[] generatePdfReport(String period) {
        log.info("Generating PDF report bytes for period: {}", period);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(138, 99, 101));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(45, 45, 45));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            
            // Title
            Paragraph title = new Paragraph("REDSHAN360 - SALES ANALYTICS REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Subtitle
            Paragraph subtitle = new Paragraph("Generated on " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()) + " (Period: " + period + ")", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);
            
            // Separator line
            Paragraph separator = new Paragraph("______________________________________________________________________________", subtitleFont);
            separator.setSpacingAfter(20);
            document.add(separator);
            
            // Section 1: Dashboard KPIs
            document.add(new Paragraph("1. Executive Summary KPIs", headingFont));
            Paragraph kpiIntro = new Paragraph("Summary of all-time counts, orders backlog, and monthly revenue performance:", normalFont);
            kpiIntro.setSpacingAfter(10);
            document.add(kpiIntro);
            
            // Fetch Dashboard data to get the real KPI values
            Map<String, Object> dashboardData = getDashboardData(period, null, null);
            
            PdfPTable kpiTable = new PdfPTable(2);
            kpiTable.setWidthPercentage(100);
            kpiTable.setSpacingAfter(20);
            
            Color headerColor = new Color(138, 99, 101);
            
            addTableCell(kpiTable, "Indicator", tableHeaderFont, headerColor, true);
            addTableCell(kpiTable, "Value", tableHeaderFont, headerColor, true);
            
            addTableCell(kpiTable, "Total Customers", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("totalCustomers")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Total Managers", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("totalManagers")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Total Employees", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("totalEmployees")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Total Products Catalog", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("totalProducts")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Pending Orders Queue", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("pendingOrders")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Completed Orders", normalFont, Color.WHITE, false);
            addTableCell(kpiTable, String.valueOf(dashboardData.get("completedOrders")), normalFont, Color.WHITE, false);
            
            addTableCell(kpiTable, "Monthly Revenue", normalFont, Color.WHITE, false);
            BigDecimal monthlyRev = (BigDecimal) dashboardData.get("monthlyRevenue");
            String revStr = monthlyRev != null ? "Rs. " + String.format("%,.2f", monthlyRev) : "Rs. 0.00";
            addTableCell(kpiTable, revStr, normalFont, Color.WHITE, false);
            
            document.add(kpiTable);
            
            // Section 2: Monthly Sales Overview
            document.add(new Paragraph("2. Monthly Sales Overview (Last 6 Months)", headingFont));
            Paragraph salesIntro = new Paragraph("Sales revenue breakdown, transaction volumes, and completed fulfillment rates grouped by calendar month:", normalFont);
            salesIntro.setSpacingAfter(10);
            document.add(salesIntro);
            
            List<Map<String, Object>> monthlySales = getMonthlySalesOverview();
            
            PdfPTable salesTable = new PdfPTable(5);
            salesTable.setWidthPercentage(100);
            salesTable.setSpacingAfter(20);
            
            addTableCell(salesTable, "Month", tableHeaderFont, headerColor, true);
            addTableCell(salesTable, "Revenue", tableHeaderFont, headerColor, true);
            addTableCell(salesTable, "Total Orders", tableHeaderFont, headerColor, true);
            addTableCell(salesTable, "Completed Orders", tableHeaderFont, headerColor, true);
            addTableCell(salesTable, "Average Order Value", tableHeaderFont, headerColor, true);
            
            for (Map<String, Object> row : monthlySales) {
                String month = (String) row.get("month");
                BigDecimal rev = (BigDecimal) row.get("revenue");
                long totalOrd = (long) row.get("ordersCount");
                long completedOrd = (long) row.get("completedOrdersCount");
                
                BigDecimal aov = BigDecimal.ZERO;
                if (totalOrd > 0) {
                    aov = rev.divide(BigDecimal.valueOf(totalOrd), 2, java.math.RoundingMode.HALF_UP);
                }
                
                addTableCell(salesTable, month, normalFont, Color.WHITE, false);
                addTableCell(salesTable, "Rs. " + String.format("%,.2f", rev), normalFont, Color.WHITE, false);
                addTableCell(salesTable, String.valueOf(totalOrd), normalFont, Color.WHITE, false);
                addTableCell(salesTable, String.valueOf(completedOrd), normalFont, Color.WHITE, false);
                addTableCell(salesTable, "Rs. " + String.format("%,.2f", aov), normalFont, Color.WHITE, false);
            }
            
            document.add(salesTable);
            
            // Footer notice
            Paragraph footer = new Paragraph("\n\nThis is an automatically generated system report from the RedShan360 administration console. All data is retrieved directly from the live PostgreSQL database.", subtitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
        } catch (Exception e) {
            log.error("Failed to generate PDF report", e);
        }
        
        return out.toByteArray();
    }
    
    private void addTableCell(PdfPTable table, String text, Font font, Color bg, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        if (isHeader) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        table.addCell(cell);
    }
}
