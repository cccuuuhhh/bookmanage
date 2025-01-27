package com.example.book_management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.book_management.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书数据访问接口
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
} 