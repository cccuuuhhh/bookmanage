package com.example.book_management.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.book_management.entity.User;
import com.example.book_management.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    public User login(String username, String password) {
        // MD5加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        
        // 查询用户
        return lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getPassword, encryptedPassword)
                .one();
    }
} 