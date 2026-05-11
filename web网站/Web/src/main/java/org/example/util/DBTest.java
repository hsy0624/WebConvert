package org.example.util;

import java.sql.Connection;

/**
 * 数据库连接测试类
 * 直接右键运行这个 main 方法，看是否能连接成功
 */
public class DBTest {
    public static void main(String[] args) {
        System.out.println("正在测试数据库连接...");
        System.out.println("连接地址: jdbc:sqlserver://localhost:1433;databaseName=web_convert");
        System.out.println("用户名: sa");
        System.out.println("-----------------------------------");

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ 数据库连接成功！");
                System.out.println("数据库产品: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！");
            System.out.println("错误信息: " + e.getMessage());
            System.out.println();
            System.out.println("==== 常见原因 ====");
            System.out.println("1. SQL Server 服务未启动");
            System.out.println("2. TCP/IP 协议未启用（端口1433）");
            System.out.println("3. sa 用户名或密码不正确");
            System.out.println("4. 数据库 web_convert 还未创建（需先执行 init.sql）");
            System.out.println("5. SQL Server 未启用混合认证模式");
        } finally {
            DBUtil.close(conn, null);
        }
    }
}
