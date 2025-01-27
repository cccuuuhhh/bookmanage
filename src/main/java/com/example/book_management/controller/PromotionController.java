package com.example.book_management.controller;

import com.example.book_management.entity.Promotion;
import com.example.book_management.service.PromotionService;
import com.example.book_management.vo.PromotionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/list")
    public List<PromotionVO> listPromotions() {
        return promotionService.listPromotions();
    }
    
    @PostMapping("/save")
    public Promotion savePromotion(@RequestBody Promotion promotion) {
        promotionService.savePromotion(promotion);
        return promotion;
    }
    
    @DeleteMapping("/delete/{id}")
    public void deletePromotion(@PathVariable Long id) {
        promotionService.removeById(id);
    }
} 