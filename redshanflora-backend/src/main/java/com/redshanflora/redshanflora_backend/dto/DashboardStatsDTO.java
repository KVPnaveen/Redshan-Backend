package com.redshanflora.redshanflora_backend.dto;

public class DashboardStatsDTO {
    private long totalOrders;
    private long totalProducts;
    private long totalEmployees;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalOrders, long totalProducts, long totalEmployees) {
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
        this.totalEmployees = totalEmployees;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }
}
