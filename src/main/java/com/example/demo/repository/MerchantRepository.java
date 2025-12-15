package com.example.demo.repository;

import com.example.demo.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    /**
     * 根据用户名查询商家
     * 
     * @param username 用户名
     * @return 商家信息
     */
    Optional<Merchant> findByUsername(String username);
}