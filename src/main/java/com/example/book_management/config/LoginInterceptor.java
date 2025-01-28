package com.example.book_management.config;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        
        // 如果是静态资源，直接放行
        if (isStaticResource(requestURI)) {
            return true;
        }
        
        // 如果是登录页面或登录API，直接放行
        if (requestURI.equals("/login.html") || requestURI.equals("/api/login")) {
            return true;
        }
        
        // 检查token是否有效
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果是API请求，返回401状态码
            if (isApiRequest(requestURI)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            // 非API请求重定向到登录页
            response.sendRedirect("/login.html");
            return false;
        }

        return true;
    }

    private boolean isStaticResource(String uri) {
        return uri.startsWith("/js/") ||
               uri.startsWith("/css/") ||
               uri.startsWith("/images/") ||
               uri.endsWith(".ico") ||
               uri.equals("/index.html") ||
               uri.equals("/promotion.html");
    }

    private boolean isApiRequest(String uri) {
        return uri.startsWith("/api/") ||
               uri.startsWith("/books/") ||
               uri.startsWith("/promotions/");
    }
} 