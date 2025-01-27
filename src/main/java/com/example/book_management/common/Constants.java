package com.example.book_management.common;

public interface Constants {
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String TOKEN_PREFIX = "Bearer ";
    String AUTH_HEADER = "Authorization";
    
    interface ErrorCode {
        int SUCCESS = 200;
        int UNAUTHORIZED = 401;
        int FORBIDDEN = 403;
        int NOT_FOUND = 404;
        int INTERNAL_ERROR = 500;
    }
} 