package com.example.book_management.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
} 