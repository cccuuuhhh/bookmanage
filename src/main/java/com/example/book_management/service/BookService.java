package com.example.book_management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.book_management.entity.Book;
import com.example.book_management.mapper.BookMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.cache.annotation.CachePut;

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
    @CachePut(value = "book", key = "#book.id")
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
        List<Book> books = list();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("图书列表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("书名");
            headerRow.createCell(2).setCellValue("作者");
            headerRow.createCell(3).setCellValue("价格");
            headerRow.createCell(4).setCellValue("ISBN");
            headerRow.createCell(5).setCellValue("库存");
            headerRow.createCell(6).setCellValue("创建时间");
            headerRow.createCell(7).setCellValue("更新时间");

            // 填充数据
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getName());
                row.createCell(2).setCellValue(book.getAuthor());
                row.createCell(3).setCellValue(book.getPrice().doubleValue());
                row.createCell(4).setCellValue(book.getIsbn());
                row.createCell(5).setCellValue(book.getStock());
                row.createCell(6).setCellValue(book.getCreateTime());
                row.createCell(7).setCellValue(book.getUpdateTime());
            }

            // 自动调整列宽
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 根据条件查询图书
     *
     * @param name     书名，支持模糊查询
     * @param author   作者，支持模糊查询
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param isbn     ISBN号，支持模糊查询
     * @return 符合条件的图书列表
     */
//    @Cacheable(value = "books", key = "#name + #author + #minPrice + #maxPrice + #isbn")
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