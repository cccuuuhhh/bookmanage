package com.example.book_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径重定向到登录页面
        registry.addRedirectViewController("/", "/login.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login.html",
                        "/api/login",
                        "/js/login.js",
                        "/js/axios.js",
                        "/js/book.js",
                        "/js/promotion.js",
                        "/css/**",
                        "/js/**",
                        "/*.ico",
                        "/error",
                        "/index.html",
                        "/promotion.html"
                );
    }
} 