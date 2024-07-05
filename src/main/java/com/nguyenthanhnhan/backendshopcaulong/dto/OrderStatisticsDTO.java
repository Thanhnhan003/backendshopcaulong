package com.nguyenthanhnhan.backendshopcaulong.dto;

public class OrderStatisticsDTO {
    private String revenue;
    private long orderCount;

    public OrderStatisticsDTO(String revenue, long orderCount) {
        this.revenue = revenue;
        this.orderCount = orderCount;
    }

    // Getters and setters là các hàm hoặc phương thức được sử dụng để lấy và đặt các giá trị của các biến
    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }
}