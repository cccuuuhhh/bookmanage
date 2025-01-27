package com.example.book_management.controller;

import com.example.book_management.entity.Book;
import com.example.book_management.service.BookService;
import com.example.book_management.service.BookPromotionService;
import com.example.book_management.vo.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书管理控制器
 */
@RestController
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BookPromotionService bookPromotionService;
    
    /**
     * 新增图书
     *
     * @param book 图书信息
     * @return 新增的图书信息
     */
    @PostMapping("/add")
    public Book addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return book;
    }
    
    /**
     * 查询图书列表
     *
     * @param name 书名
     * @param author 作者
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param isbn ISBN
     * @return 图书列表
     */
    @GetMapping("/list")
    public List<BookVO> listBooks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String isbn) {
        return bookPromotionService.searchBooksWithPromotion(name, author, minPrice, maxPrice, isbn);
    }
    
    /**
     * 保存图书信息
     *
     * @param book 图书信息
     * @return 保存后的图书信息
     */
    @PostMapping("/save")
    public Book saveBook(@RequestBody Book book) {
        bookService.saveBook(book);
        return book;
    }
    
    /**
     * 删除图书
     *
     * @param id 图书ID
     */
    @DeleteMapping("/delete/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.removeById(id);
    }
    
    /**
     * 导出图书列表到Excel
     *
     * @return Excel文件的响应体
     * @throws Exception 导出异常时抛出
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() throws Exception {
        byte[] excelContent = bookService.exportToExcel();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
} 