CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '图书名称',
    author VARCHAR(255) NOT NULL COMMENT '作者姓名',
    price DECIMAL(10,2) NOT NULL COMMENT '图书价格',
    isbn VARCHAR(20) NOT NULL COMMENT '国际标准书号',
    stock INTEGER NOT NULL DEFAULT 0 COMMENT '库存数量',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
); 

-- 表说明：图书信息表
-- 字段说明：
-- id: 自增主键
-- name: 图书名称，不能为空
-- author: 作者姓名，不能为空
-- price: 图书价格，不能为空
-- isbn: 国际标准书号，不能为空
-- stock: 库存数量，不能为空，默认为0
-- create_time: 记录创建时间
-- update_time: 记录最后更新时间 

COMMENT ON TABLE book IS '图书信息表';

CREATE TABLE IF NOT EXISTS promotion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    discount DECIMAL(3,2) NOT NULL COMMENT '促销折扣',
    start_time DATETIME NOT NULL COMMENT '促销开始时间',
    end_time DATETIME NOT NULL COMMENT '促销结束时间',
    description TEXT COMMENT '促销说明',
    FOREIGN KEY (book_id) REFERENCES book(id)
); 

-- 表说明：图书促销表
-- 字段说明：
-- id: 自增主键
-- book_id: 图书ID，不能为空
-- discount: 促销折扣，不能为空
-- start_time: 促销开始时间，不能为空
-- end_time: 促销结束时间，不能为空
-- description: 促销说明
-- FOREIGN KEY (book_id) REFERENCES book(id): 外键约束，引用book表的id字段 

COMMENT ON TABLE promotion IS '图书促销表'; 

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) COMMENT='用户表';

-- 初始化管理员账号，密码为：admin123
INSERT INTO user (username, password, create_time, update_time) 
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW(); 
