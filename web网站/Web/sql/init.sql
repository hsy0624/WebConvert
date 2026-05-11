-- ============================================
-- 格式转换工具 - 数据库初始化脚本（SQL Server 2014）
-- 请在 SQL Server Management Studio (SSMS) 中执行
-- ============================================

-- 1. 创建数据库
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'web_convert')
BEGIN
    CREATE DATABASE web_convert;
END
GO

-- 2. 切换到该数据库
USE web_convert;
GO

-- ============================================
-- 3. 用户表
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user')
BEGIN
    CREATE TABLE [user] (
        id          INT IDENTITY(1,1) PRIMARY KEY,   -- 用户ID，自增
        username    NVARCHAR(50) NOT NULL UNIQUE,     -- 用户名
        password    NVARCHAR(100) NOT NULL,           -- 密码
        create_time DATETIME DEFAULT GETDATE()        -- 注册时间
    );
END
GO

-- ============================================
-- 4. 转换记录表
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'convert_log')
BEGIN
    CREATE TABLE convert_log (
        id          INT IDENTITY(1,1) PRIMARY KEY,   -- 记录ID，自增
        user_id     INT NOT NULL,                     -- 用户ID
        type        NVARCHAR(50) NOT NULL,            -- 转换类型
        original    NTEXT NOT NULL,                   -- 原始内容
        result      NTEXT NOT NULL,                   -- 转换结果
        create_time DATETIME DEFAULT GETDATE(),       -- 转换时间
        FOREIGN KEY (user_id) REFERENCES [user](id) ON DELETE CASCADE
    );
END
GO

-- 5. 插入管理员账号（用户名: admin, 密码: admin123）
IF NOT EXISTS (SELECT * FROM [user] WHERE username = 'admin')
BEGIN
    INSERT INTO [user] (username, password) VALUES ('admin', 'admin123');
END
GO
