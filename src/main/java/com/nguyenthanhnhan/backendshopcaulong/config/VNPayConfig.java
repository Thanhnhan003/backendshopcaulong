package com.nguyenthanhnhan.backendshopcaulong.config;


import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {


    private String vnp_TmnCode = "BGQ7P8CP";
    private String vnp_HashSecret = "766N5CEJRHQQ5AZB9T9U0QHQJZQF6PP7";
    private String vnp_ApiUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private String vnp_ReturnUrl = "http://localhost:3000/orders/return";

    public String getTmnCode() {
        return vnp_TmnCode;
    }

    public String getHashSecret() {
        return vnp_HashSecret;
    }

    public String getApiUrl() {
        return vnp_ApiUrl;
    }

    public String getReturnUrl() {
        return vnp_ReturnUrl;
    }
}
