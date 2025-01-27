package com.example.book_management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.book_management.entity.Book;
import com.example.book_management.mapper.BookMapper;
import lombok.var;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.example.book_management.vo.BookVO;
import com.example.book_management.entity.Promotion;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 图书服务类
 */
@Service
public class BookService extends ServiceImpl<BookMapper, Book> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final BookMapper bookMapper;

    @Autowired
    private PromotionService promotionService; // 注入PromotionService

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    /**
     * 新增图书
     *
     * @param book 图书信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addBook(Book book) {
        // 设置创建时间和更新时间
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        book.setCreateTime(now);
        book.setUpdateTime(now);
        
        // 验证必填字段
        if (StringUtils.hasText(book.getName()) 
            && StringUtils.hasText(book.getAuthor()) 
            && StringUtils.hasText(book.getIsbn()) 
            && book.getPrice() != null
            && book.getStock() != null
            && book.getStock() >= 0) {
            save(book);
        } else {
            throw new IllegalArgumentException("必填字段不能为空，库存不能为负数");
        }
    }
    
    /**
     * 保存或更新图书信息
     * 新增时自动设置创建时间
     * 更新时自动设置更新时间
     *
     * @param book 图书信息
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "books", allEntries = true) // 清除所有图书缓存
    public void saveBook(Book book) {
        if (book.getId() == null) {
            book.setCreateTime(LocalDateTime.now().format(DATE_FORMATTER));
            book.setUpdateTime(LocalDateTime.now().format(DATE_FORMATTER));
            save(book);
        } else {
            book.setUpdateTime(LocalDateTime.now().format(DATE_FORMATTER));
            updateById(book);
        }
    }

    /**
     * 导出图书列表到Excel
     *
     * @return Excel文件的字节数组
     * @throws IOException 导出异常时抛出
     */
    public byte[] exportToExcel() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("图书列表");
        
        // 创建表头
        var headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("书名");
        headerRow.createCell(2).setCellValue("作者");
        headerRow.createCell(3).setCellValue("价格");
        headerRow.createCell(4).setCellValue("折扣价");
        headerRow.createCell(5).setCellValue("ISBN");
        
        // 创建红色字体样式
        var font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        var redStyle = workbook.createCellStyle();
        redStyle.setFont(font);
        
        // 获取当前时间
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        
        // 填充数据
        var books = list();
        for (int i = 0; i < books.size(); i++) {
            var book = books.get(i);
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getName());
            row.createCell(2).setCellValue(book.getAuthor());
            row.createCell(3).setCellValue(book.getPrice().doubleValue());
            
            // 计算并设置折扣价
            var discountPriceCell = row.createCell(4);
            Promotion promotion = promotionService.getCurrentPromotion(book.getId(), now); // 传递bookId和当前时间
            if (promotion != null && promotion.getDiscount() != null) {
                double discountPrice = book.getPrice().doubleValue() * promotion.getDiscount().doubleValue();
                discountPriceCell.setCellValue(discountPrice);
                discountPriceCell.setCellStyle(redStyle);
            } else {
                discountPriceCell.setCellValue("无折扣");
            }
            
            row.createCell(5).setCellValue(book.getIsbn());
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 根据条件查询图书
     *
     * @param name 书名，支持模糊查询
     * @param author 作者，支持模糊查询
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param isbn ISBN号，支持模糊查询
     * @return 符合条件的图书列表
     */
    public List<Book> searchBooks(String name, String author, BigDecimal minPrice, BigDecimal maxPrice, String isbn) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Book::getName, "%" + name + "%");
        }
        
        if (StringUtils.hasText(author)) {
            queryWrapper.like(Book::getAuthor, "%" + author + "%");
        }
        
        if (minPrice != null) {
            queryWrapper.ge(Book::getPrice, minPrice);
        }
        
        if (maxPrice != null) {
            queryWrapper.le(Book::getPrice, maxPrice);
        }
        
        if (StringUtils.hasText(isbn)) {
            queryWrapper.like(Book::getIsbn, "%" + isbn + "%");
        }
        
        return list(queryWrapper);
    }

    @Cacheable(value = "books", key = "#id") // 缓存图书信息
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    @Cacheable(value = "books") // 缓存所有图书列表
    public List<Book> listBooks() {
        return bookMapper.selectList(null);
    }

    @CacheEvict(value = "books", key = "#id") // 清除特定图书缓存
    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }
} 