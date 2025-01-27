package com.example.book_management.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class DataInitConfig {

    @PostConstruct
    public void init() {
        // 创建数据目录
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建数据目录");
            }
        }
        // 检查目录权限
        if (!dataDir.canWrite() || !dataDir.canRead()) {
            throw new RuntimeException("数据目录没有读写权限");
        }
    }
} 