package com.nguyenthanhnhan.backendshopcaulong.dto;

public class UserStatisticsDTO {
    private long adminCount;
    private long userCount;
    private long totalCount;

    public UserStatisticsDTO(long adminCount, long userCount) {
        this.adminCount = adminCount;
        this.userCount = userCount;
        this.totalCount = adminCount + userCount;
    }

    // Getters and setters là các hàm hoặc phương thức được sử dụng để lấy và đặt các giá trị của các biến

    public long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(long adminCount) {
        this.adminCount = adminCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}