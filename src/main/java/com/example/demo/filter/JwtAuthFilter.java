package com.example.demo.filter;

// import com.example.demo.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 简单实现：暂时直接从请求头获取商家ID，不做JWT验证
        String merchantIdStr = request.getHeader("X-Merchant-Id");
        if (merchantIdStr != null && !merchantIdStr.isEmpty()) {
            try {
                Long merchantId = Long.parseLong(merchantIdStr);
                request.setAttribute("X-Merchant-Id", merchantId);
            } catch (NumberFormatException e) {
                // 忽略无效的商家ID
            }
        }

        filterChain.doFilter(request, response);
    }
}
