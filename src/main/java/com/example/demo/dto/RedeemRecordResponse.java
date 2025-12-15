package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedeemRecordResponse {
    private String code;
    private String goodsName;
    private String visitorId;
    private LocalDateTime redeemTime;
    
    public RedeemRecordResponse(String code, String goodsName, String visitorId, LocalDateTime redeemTime) {
        this.code = code;
        this.goodsName = goodsName;
        // 对游客ID进行脱敏处理
        this.visitorId = maskVisitorId(visitorId);
        this.redeemTime = redeemTime;
    }
    
    private String maskVisitorId(String visitorId) {
        if (visitorId == null || visitorId.length() <= 4) {
            return "****";
        }
        return visitorId.substring(0, 2) + "****" + visitorId.substring(visitorId.length() - 2);
    }
}