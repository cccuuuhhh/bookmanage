package com.example.book_management.vo;

import com.example.book_management.entity.Book;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PromotionVO {
    private Long id;
    private Long bookId;
    private Book book;
    private BigDecimal discount;
    private String startTime;
    private String endTime;
    private String description;
} 