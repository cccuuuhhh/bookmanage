package com.example.book_management.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.book_management.entity.Book;
import com.example.book_management.entity.Promotion;
import com.example.book_management.mapper.PromotionMapper;
import com.example.book_management.vo.PromotionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService extends ServiceImpl<PromotionMapper, Promotion> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private BookService bookService;
    
    @Cacheable(value = "promotions", key = "'all'")
    public List<PromotionVO> listPromotions() {
        List<Promotion> promotions = list();
        return promotions.stream().map(promotion -> {
            PromotionVO vo = new PromotionVO();
            BeanUtils.copyProperties(promotion, vo);
            // 获取关联的图书信息
            Book book = bookService.getById(promotion.getBookId());
            vo.setBook(book);
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = "promotion", key = "#promotion.id")
    @CacheEvict(value = "promotions", allEntries = true)
    public void savePromotion(Promotion promotion) {
        // 验证图书是否存在
        Book book = bookService.getById(promotion.getBookId());
        if (book == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        
        // 验证时间格式和逻辑
        try {
            LocalDateTime startTime = LocalDateTime.parse(promotion.getStartTime(), DATE_PARSER);
            LocalDateTime endTime = LocalDateTime.parse(promotion.getEndTime(), DATE_PARSER);
            
            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }
            
            // 格式化时间
            promotion.setStartTime(startTime.format(DATE_FORMATTER));
            promotion.setEndTime(endTime.format(DATE_FORMATTER));
        } catch (Exception e) {
            throw new IllegalArgumentException("时间格式不正确: " + e.getMessage());
        }
        
        // 验证折扣
        if (promotion.getDiscount().compareTo(BigDecimal.ZERO) <= 0 
            || promotion.getDiscount().compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("折扣必须在0-1之间");
        }
        
        saveOrUpdate(promotion);
    }

    /**
     * 获取图书当前有效的促销信息
     *
     * @param bookId 图书ID
     * @param currentTime 当前时间
     * @return 促销信息，如果没有则返回null
     */
    @Cacheable(value = "promotion", key = "#bookId + ':' + #currentTime")
    public Promotion getCurrentPromotion(Long bookId, String currentTime) {
        return lambdaQuery()
            .eq(Promotion::getBookId, bookId)
            .le(Promotion::getStartTime, currentTime)
            .ge(Promotion::getEndTime, currentTime)
            .orderByDesc(Promotion::getDiscount)
            .last("LIMIT 1")
            .one();
    }
    
    @CacheEvict(value = {"promotion", "promotions"}, allEntries = true)
    public boolean removeById(Long id) {
        return super.removeById(id);
    }
} 