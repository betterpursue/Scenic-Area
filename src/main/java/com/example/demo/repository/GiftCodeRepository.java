package com.example.demo.repository;

import com.example.demo.entity.GiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCode, Long> {

    Optional<GiftCode> findByCode(String code);

    @Modifying
    @Query("UPDATE GiftCode g SET g.status = 1, g.redeemTime = :redeemTime, g.merchantId = :merchantId WHERE g.code = :code")
    int redeemCode(@Param("code") String code, @Param("redeemTime") LocalDateTime redeemTime, @Param("merchantId") Long merchantId);

    @Query("SELECT g FROM GiftCode g WHERE g.merchantId = :merchantId AND g.status = 1 ORDER BY g.redeemTime DESC")
    List<GiftCode> findRedeemedCodesByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT g FROM GiftCode g WHERE g.merchantId = :merchantId AND g.status = 1 AND g.redeemTime BETWEEN :startDate AND :endDate ORDER BY g.redeemTime DESC")
    List<GiftCode> findRedeemedCodesByMerchantIdAndDateRange(
            @Param("merchantId") Long merchantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
