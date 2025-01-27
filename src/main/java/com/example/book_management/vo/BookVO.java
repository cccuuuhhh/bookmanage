package com.example.book_management.vo;

import com.example.book_management.entity.Book;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookVO extends Book {
    /**
     * 是否促销中
     */
    private boolean onPromotion;
    
    /**
     * 促销折扣
     */
    private BigDecimal discount;
    
    /**
     * 促销价格
     */
    private BigDecimal promotionPrice;
} 