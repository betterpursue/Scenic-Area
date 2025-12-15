package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private Long merchantId;
    private String merchantName;

    public LoginResponse(String token, Long merchantId, String merchantName) {
        this.token = token;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }
}
