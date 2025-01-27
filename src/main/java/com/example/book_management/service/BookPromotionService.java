package com.example.book_management.service;

import com.example.book_management.entity.Book;
import com.example.book_management.entity.Promotion;
import com.example.book_management.vo.BookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookPromotionService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private PromotionService promotionService;
    
    public List<BookVO> listBooksWithPromotion() {
        List<Book> books = bookService.list();
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        
        return books.stream().map(book -> {
            BookVO vo = new BookVO();
            BeanUtils.copyProperties(book, vo);
            
            // 获取当前有效的促销信息
            Promotion promotion = promotionService.getCurrentPromotion(book.getId(), now);
            if (promotion != null) {
                vo.setOnPromotion(true);
                vo.setDiscount(promotion.getDiscount());
                vo.setPromotionPrice(book.getPrice().multiply(promotion.getDiscount()));
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    public List<BookVO> searchBooksWithPromotion(String name, String author, BigDecimal minPrice, BigDecimal maxPrice, String isbn) {
        List<Book> books = bookService.searchBooks(name, author, minPrice, maxPrice, isbn);
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        
        return books.stream().map(book -> {
            BookVO vo = new BookVO();
            BeanUtils.copyProperties(book, vo);
            
            // 获取当前有效的促销信息
            Promotion promotion = promotionService.getCurrentPromotion(book.getId(), now);
            if (promotion != null) {
                vo.setOnPromotion(true);
                vo.setDiscount(promotion.getDiscount());
                vo.setPromotionPrice(book.getPrice().multiply(promotion.getDiscount()));
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
} 