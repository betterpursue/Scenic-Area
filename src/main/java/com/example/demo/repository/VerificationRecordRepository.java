package com.example.demo.repository;

import com.example.demo.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, Long> {
    
    @Query("SELECT v FROM VerificationRecord v WHERE v.merchantId = :merchantId ORDER BY v.verifyTime DESC")
    List<VerificationRecord> findByMerchantIdOrderByVerifyTimeDesc(@Param("merchantId") Long merchantId);
    
    @Query("SELECT v FROM VerificationRecord v WHERE v.merchantId = :merchantId AND v.verifyTime BETWEEN :startDate AND :endDate ORDER BY v.verifyTime DESC")
    List<VerificationRecord> findByMerchantIdAndDateRange(
        @Param("merchantId") Long merchantId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT DATE(v.verifyTime) as date, COUNT(v) as count FROM VerificationRecord v WHERE v.merchantId = :merchantId AND v.verifyTime >= :startDate GROUP BY DATE(v.verifyTime) ORDER BY date")
    List<Object[]> findDailyStatistics(@Param("merchantId") Long merchantId, @Param("startDate") LocalDateTime startDate);
    
    @Query(value = "SELECT CONCAT(YEAR(v.verify_time), '-', ISO_WEEK(v.verify_time)) as week, COUNT(v.id) as count FROM verification_record v WHERE v.merchant_id = :merchantId AND v.verify_time >= :startDate GROUP BY week ORDER BY week", nativeQuery = true)
    List<Object[]> findWeeklyStatistics(@Param("merchantId") Long merchantId, @Param("startDate") LocalDateTime startDate);
    
    @Query(value = "SELECT FORMATDATETIME(v.verify_time, 'yyyy-MM') as stat_month, COUNT(v.id) as count FROM verification_record v WHERE v.merchant_id = :merchantId AND v.verify_time >= :startDate GROUP BY stat_month ORDER BY stat_month", nativeQuery = true)
    List<Object[]> findMonthlyStatistics(@Param("merchantId") Long merchantId, @Param("startDate") LocalDateTime startDate);
}