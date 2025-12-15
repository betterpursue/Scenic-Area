package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Merchant;
import com.example.demo.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    /**
     * 商家登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询商家
        Merchant merchant = merchantRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码
        // 简化实现：直接比较密码，实际项目中应该使用PasswordEncoder进行密码加密和验证
        if (!merchant.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 简化实现：直接返回模拟Token，实际项目中应该使用JWT生成真实Token
        String token = "mock-token-" + merchant.getId();
        
        return new LoginResponse(token, merchant.getId(), merchant.getMerchantName());
    }
}