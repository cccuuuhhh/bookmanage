package com.example.book_management.config;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        // 如果是登录页面或登录API，直接放行
        if (requestURI.equals("/login.html") || 
            requestURI.equals("/api/login") ||
            requestURI.startsWith("/js/") ||
            requestURI.startsWith("/css/") ||
            requestURI.equals("/index.html") ||
            requestURI.equals("/promotion.html")) {
            return true;
        }
        
        // 检查token是否有效
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果是API请求，返回401状态码
            if (requestURI.startsWith("/api/") || requestURI.startsWith("/books/") || requestURI.startsWith("/promotions/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            // 非API请求重定向到登录页
            response.sendRedirect("/login.html");
            return false;
        }

        return true;
    }
} 