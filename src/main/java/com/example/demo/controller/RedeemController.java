package com.example.demo.controller;

import com.example.demo.dto.RedeemRecordResponse;
import com.example.demo.dto.RedeemRequest;
import com.example.demo.dto.StatisticsResponse;
import com.example.demo.service.RedeemService;
import com.example.demo.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/redeem")
public class RedeemController {

    private static final Logger logger = LoggerFactory.getLogger(RedeemController.class);

    @Autowired
    private RedeemService redeemService;

    @PostMapping
    public Result<Void> redeemCode(@Valid @RequestBody RedeemRequest request, HttpServletRequest httpRequest) {
        Long merchantId = (Long) httpRequest.getAttribute("X-Merchant-Id");
        if (merchantId == null) {
            String merchantIdHeader = httpRequest.getHeader("X-Merchant-Id");
            if (merchantIdHeader != null) {
                try {
                    merchantId = Long.parseLong(merchantIdHeader);
                } catch (NumberFormatException e) {
                    logger.error("无效的商家ID头：{}", merchantIdHeader);
                    return Result.error(400, "无效的商家ID");
                }
            }
        }
        if (merchantId == null) {
            logger.error("商家认证失败，商家ID为null");
            return Result.error(403, "商家认证失败");
        }

        redeemService.redeemCode(request, merchantId);

        logger.info("核销请求处理成功，礼品码：{}", request.getCode());
        return Result.<Void>success("核销成功", null);
    }

    @GetMapping("/records")
    public Result<List<RedeemRecordResponse>> getRedeemRecords(
            HttpServletRequest httpRequest,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Long merchantId = (Long) httpRequest.getAttribute("X-Merchant-Id");
        if (merchantId == null) {
            String merchantIdHeader = httpRequest.getHeader("X-Merchant-Id");
            if (merchantIdHeader != null) {
                try {
                    merchantId = Long.parseLong(merchantIdHeader);
                } catch (NumberFormatException e) {
                    logger.error("无效的商家ID头：{}", merchantIdHeader);
                    return Result.error(400, "无效的商家ID");
                }
            }
        }
        if (merchantId == null) {
            logger.error("商家认证失败，商家ID为null");
            return Result.error(403, "商家认证失败");
        }

        List<RedeemRecordResponse> records = redeemService.getRedeemRecords(merchantId, startDate, endDate, page, size);

        logger.info("核销记录查询请求处理成功，返回记录数量：{}", records.size());
        return Result.success(records);
    }

    @GetMapping("/statistics")
    public Result<StatisticsResponse> getStatistics(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "WEEK") String period) {
        
        logger.info("接收核销统计查询请求，统计周期：{}", period);
        
        // 从请求属性获取商家ID
        Long merchantId = (Long) httpRequest.getAttribute("X-Merchant-Id");
        if (merchantId == null) {
            String merchantIdHeader = httpRequest.getHeader("X-Merchant-Id");
            if (merchantIdHeader != null) {
                try {
                    merchantId = Long.parseLong(merchantIdHeader);
                } catch (NumberFormatException e) {
                    logger.error("无效的商家ID头：{}", merchantIdHeader);
                    return Result.error(400, "无效的商家ID");
                }
            }
        }
        if (merchantId == null) {
            logger.error("商家认证失败，商家ID为null");
            return Result.error(403, "商家认证失败");
        }
        
        StatisticsResponse statistics = redeemService.getStatistics(merchantId, period);
        
        logger.info("核销统计查询请求处理成功");
        return Result.success(statistics);
    }
}
