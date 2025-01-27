package com.example.book_management.controller;

import com.example.book_management.dto.LoginRequest;
import com.example.book_management.dto.LoginResponse;
import com.example.book_management.entity.User;
import com.example.book_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        
        if (user == null) {
            return ResponseEntity.badRequest().body("用户名或密码错误");
        }
        
        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        // 生成简单的token，实际项目中应该使用JWT
        response.setToken(UUID.randomUUID().toString());
        
        return ResponseEntity.ok(response);
    }
} 