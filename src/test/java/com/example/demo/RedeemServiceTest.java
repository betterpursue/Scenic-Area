package com.example.demo;

import com.example.demo.dto.RedeemRequest;
import com.example.demo.dto.StatisticsResponse;
import com.example.demo.entity.GiftCode;
import com.example.demo.entity.VerificationRecord;
import com.example.demo.repository.GiftCodeRepository;
import com.example.demo.repository.VerificationRecordRepository;
import com.example.demo.service.RedeemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.demo.exception.RedemptionCodeInvalidException;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedeemServiceTest {

    @Mock
    private GiftCodeRepository giftCodeRepository;

    @Mock
    private VerificationRecordRepository verificationRecordRepository;

    @InjectMocks
    private RedeemService redeemService;

    @Test
    void testRedeemCodeSuccess() {
        // 准备测试数据
        RedeemRequest request = new RedeemRequest();
        request.setCode("TEST123456");

        Long merchantId = 1L;
        LocalDateTime now = LocalDateTime.now();

        GiftCode giftCode = new GiftCode();
        giftCode.setId(1L);
        giftCode.setCode("TEST123456");
        giftCode.setStatus(0);
        giftCode.setExpireTime(now.plusDays(30));
        giftCode.setGoodsName("测试商品");
        giftCode.setMerchantId(merchantId);

        // 模拟Repository方法调用
        when(giftCodeRepository.findByCode(eq("TEST123456"))).thenReturn(Optional.of(giftCode));
        when(giftCodeRepository.redeemCode(eq("TEST123456"), any(LocalDateTime.class), eq(merchantId))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> redeemService.redeemCode(request, merchantId));

        // 验证方法调用
        verify(giftCodeRepository, times(1)).findByCode(eq("TEST123456"));
        verify(giftCodeRepository, times(1)).redeemCode(eq("TEST123456"), any(LocalDateTime.class), eq(merchantId));
        verify(verificationRecordRepository, times(1)).save(any(VerificationRecord.class));
    }

    @Test
    void testGetStatistics() {
        // 准备测试数据
        Long merchantId = 1L;
        String period = "WEEK";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusWeeks(1);

        GiftCode giftCode = new GiftCode();
        giftCode.setId(1L);
        giftCode.setCode("TEST123456");
        giftCode.setStatus(1);
        giftCode.setExpireTime(now.plusDays(30));
        giftCode.setGoodsName("测试商品");
        giftCode.setRedeemTime(now);
        giftCode.setMerchantId(merchantId); // Set merchant ID for this gift code

        // 模拟Repository方法调用
        when(giftCodeRepository.findRedeemedCodesByMerchantIdAndDateRange(eq(merchantId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(giftCode));

        when(verificationRecordRepository.findDailyStatistics(eq(merchantId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        when(verificationRecordRepository.findWeeklyStatistics(eq(merchantId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        when(verificationRecordRepository.findMonthlyStatistics(eq(merchantId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        StatisticsResponse response = redeemService.getStatistics(merchantId, period);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getTotalRedeemed());
        assertEquals(1L, response.getTotalGiftTypes());
        assertNotNull(response.getDailyTrends());
        assertNotNull(response.getWeeklyTrends());
        assertNotNull(response.getMonthlyTrends());

        // 验证方法调用
        verify(giftCodeRepository, times(1))
                .findRedeemedCodesByMerchantIdAndDateRange(eq(merchantId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(verificationRecordRepository, times(1)).findDailyStatistics(eq(merchantId), any(LocalDateTime.class));
        verify(verificationRecordRepository, times(1)).findWeeklyStatistics(eq(merchantId), any(LocalDateTime.class));
        verify(verificationRecordRepository, times(1)).findMonthlyStatistics(eq(merchantId), any(LocalDateTime.class));
    }

    @Test
    void testRedeemCodeUnauthorizedMerchant() {
        // 准备测试数据
        RedeemRequest request = new RedeemRequest();
        request.setCode("TESTCODE456"); // Assuming a code that exists

        Long authorizedMerchantId = 1L;
        Long unauthorizedMerchantId = 2L;

        GiftCode giftCode = new GiftCode();
        giftCode.setId(2L);
        giftCode.setCode("TESTCODE456");
        giftCode.setStatus(0);
        giftCode.setExpireTime(LocalDateTime.now().plusDays(30));
        giftCode.setGoodsName("Unauthorized Goods");
        giftCode.setMerchantId(authorizedMerchantId); // Code belongs to authorizedMerchantId

        // 模拟Repository方法调用
        when(giftCodeRepository.findByCode(eq("TESTCODE456"))).thenReturn(Optional.of(giftCode));

        // 执行测试并验证抛出异常
        RedemptionCodeInvalidException thrown = assertThrows(
                RedemptionCodeInvalidException.class,
                () -> redeemService.redeemCode(request, unauthorizedMerchantId)
        );

        assertEquals("礼品码不属于当前商家", thrown.getMessage());

        // 验证没有尝试更新礼品码状态或创建核销记录
        verify(giftCodeRepository, never()).redeemCode(any(), any(), any());
        verify(verificationRecordRepository, never()).save(any(VerificationRecord.class));
    }
}
