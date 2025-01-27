package com.example.book_management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 图书促销实体类
 */
@Data
@TableName("promotion")
public class Promotion {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 促销折扣（例如：0.8表示8折）
     */
    private BigDecimal discount;

    /**
     * 促销开始时间
     */
    private String startTime;

    /**
     * 促销结束时间
     */
    private String endTime;

    /**
     * 促销说明
     */
    private String description;
} 