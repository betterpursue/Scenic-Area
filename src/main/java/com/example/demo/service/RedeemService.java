package com.example.demo.service;

import com.example.demo.dto.RedeemRecordResponse;
import com.example.demo.dto.RedeemRequest;
import com.example.demo.dto.StatisticsResponse;
import com.example.demo.entity.GiftCode;
import com.example.demo.entity.VerificationRecord;
import com.example.demo.exception.RedemptionCodeAlreadyUsedException;
import com.example.demo.exception.RedemptionCodeExpiredException;
import com.example.demo.exception.RedemptionCodeInvalidException;
import com.example.demo.repository.GiftCodeRepository;
import com.example.demo.repository.VerificationRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


@Service
public class RedeemService {

    private static final Logger logger = LoggerFactory.getLogger(RedeemService.class);

    @Autowired
    private GiftCodeRepository giftCodeRepository;

    @Autowired
    private VerificationRecordRepository verificationRecordRepository;

    @Transactional
    public void redeemCode(RedeemRequest request, Long merchantId) {
        logger.info("开始核销礼品码，商家ID：{}, 礼品码：{}", merchantId, request.getCode());

        // 查找兑换码
        GiftCode giftCode = giftCodeRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RedemptionCodeInvalidException("兑换码不存在"));
        logger.debug("找到礼品码：{}，状态：{}", giftCode.getCode(), giftCode.getStatus());

        // 检查是否已核销
        if (giftCode.getStatus() == 1) {
            logger.warn("礼品码已被核销，商家ID：{}, 礼品码：{}", merchantId, request.getCode());
            throw new RedemptionCodeAlreadyUsedException("兑换码已被核销");
        }

        // 检查是否属于当前商家
        if (!giftCode.getMerchantId().equals(merchantId)) {
            logger.warn("礼品码不属于当前商家，商家ID：{}, 礼品码：{}", merchantId, request.getCode());
            throw new RedemptionCodeInvalidException("礼品码不属于当前商家");
        }


        // 更新兑换码状态
        int updated = giftCodeRepository.redeemCode(
                request.getCode(),
                LocalDateTime.now(),
                merchantId
        );

        if (updated == 0) {
            logger.error("核销失败，请重试，商家ID：{}, 礼品码：{}", merchantId, request.getCode());
            throw new RuntimeException("核销失败，请重试");
        }

        // 创建核销记录
        VerificationRecord record = new VerificationRecord();
        record.setMerchantId(merchantId);
        record.setCode(request.getCode());
        record.setGoodsName(giftCode.getGoodsName());
        record.setVisitorId("VISITOR_" + request.getCode().substring(0, 8)); // 模拟游客ID
        verificationRecordRepository.save(record);

        logger.info("礼品码核销成功，商家ID：{}, 礼品码：{}", merchantId, request.getCode());
    }

    public List<RedeemRecordResponse> getRedeemRecords(Long merchantId, LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer size) {
        logger.info("开始查询核销记录，商家ID：{}, 开始日期：{}, 结束日期：{}, 页码：{}, 每页数量：{}",
                merchantId, startDate, endDate, page, size);

        // 创建Pageable对象
        Pageable pageable = PageRequest.of(page - 1, size);

        List<GiftCode> giftCodes;
        if (startDate != null && endDate != null) {
            giftCodes = giftCodeRepository.findRedeemedCodesByMerchantIdAndDateRange(merchantId, startDate, endDate, pageable);
        } else {
            giftCodes = giftCodeRepository.findRedeemedCodesByMerchantId(merchantId, pageable);
        }

        List<RedeemRecordResponse> result = giftCodes.stream()
                .map(giftCode -> new RedeemRecordResponse(
                giftCode.getCode(),
                giftCode.getGoodsName(),
                "VISITOR_" + giftCode.getCode().substring(0, 8), // 模拟游客ID
                giftCode.getRedeemTime()
        ))
                .collect(Collectors.toList());

        logger.info("查询核销记录完成，商家ID：{}, 共查询到 {} 条记录", merchantId, result.size());
        return result;
    }

    public StatisticsResponse getStatistics(Long merchantId, String period) {
        logger.info("开始查询核销统计数据，商家ID：{}, 统计周期：{}", merchantId, period);

        LocalDateTime startDate;
        LocalDateTime now = LocalDateTime.now();

        switch (period.toUpperCase()) {
            case "DAY":
                startDate = now.minusDays(1);
                logger.debug("统计周期为日，开始日期：{}", startDate);
                break;
            case "WEEK":
                startDate = now.minusWeeks(1);
                logger.debug("统计周期为周，开始日期：{}", startDate);
                break;
            case "MONTH":
                startDate = now.minusMonths(1);
                logger.debug("统计周期为月，开始日期：{}", startDate);
                break;
            default:
                startDate = now.minusWeeks(1); // 默认一周
                logger.debug("统计周期为默认（一周），开始日期：{}", startDate);
        }

        // 获取核销总数
        List<GiftCode> redeemedCodes = giftCodeRepository.findRedeemedCodesByMerchantIdAndDateRange(
                merchantId, startDate, now, Pageable.unpaged()
        );
        Long totalRedeemed = (long) redeemedCodes.size();
        logger.info("获取到核销总数：{}", totalRedeemed);

        // 获取礼品种类数
        Long totalGiftTypes = redeemedCodes.stream()
                .map(GiftCode::getGoodsName)
                .filter(goodsName -> goodsName != null && !goodsName.isEmpty())
                .distinct()
                .count();
        logger.info("获取到礼品种类数：{}", totalGiftTypes);

        // 获取每日趋势
        List<Object[]> dailyStats = verificationRecordRepository.findDailyStatistics(merchantId, startDate);
        List<StatisticsResponse.DailyTrend> dailyTrends = dailyStats.stream()
                .map(stat -> new StatisticsResponse.DailyTrend(
                stat[0].toString(),
                (Long) stat[1]
        ))
                .collect(Collectors.toList());
        logger.info("获取到每日趋势数据，共 {} 条", dailyTrends.size());

        // 获取周趋势
        List<Object[]> weeklyStats = verificationRecordRepository.findWeeklyStatistics(merchantId, startDate);
        List<StatisticsResponse.WeeklyTrend> weeklyTrends = weeklyStats.stream()
                .map(stat -> new StatisticsResponse.WeeklyTrend(
                stat[0].toString(),
                (Long) stat[1]
        ))
                .collect(Collectors.toList());
        logger.info("获取到周趋势数据，共 {} 条", weeklyTrends.size());

        // 获取月趋势
        List<Object[]> monthlyStats = verificationRecordRepository.findMonthlyStatistics(merchantId, startDate);
        List<StatisticsResponse.MonthlyTrend> monthlyTrends = monthlyStats.stream()
                .map(stat -> new StatisticsResponse.MonthlyTrend(
                stat[0].toString(),
                (Long) stat[1]
        ))
                .collect(Collectors.toList());
        logger.info("获取到月趋势数据，共 {} 条", monthlyTrends.size());

        StatisticsResponse response = new StatisticsResponse();
        response.setTotalRedeemed(totalRedeemed);
        response.setTotalGiftTypes(totalGiftTypes);
        response.setDailyTrends(dailyTrends);
        response.setWeeklyTrends(weeklyTrends);
        response.setMonthlyTrends(monthlyTrends);

        logger.info("核销统计数据查询完成，商家ID：{}", merchantId);
        return response;
    }
}
