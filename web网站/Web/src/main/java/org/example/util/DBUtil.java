package org.example.util;

import java.sql.*;

/**
 * 数据库工具类 - 使用 JDBC 连接 SQL Server 2014
 * 提供获取连接、关闭资源的方法
 */
public class DBUtil {
    // ===== 数据库连接配置，请根据实际情况修改 =====
    // SQL Server 连接地址：localhost 是服务器地址，1433 是默认端口，web_convert 是数据库名
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=web_convert;encrypt=false";
    private static final String USERNAME = "sa";          // SQL Server 用户名（默认sa）
    private static final String PASSWORD = "123gfdyyuyy";      // SQL Server 密码（安装时设置的）

    // 静态代码块：加载 SQL Server 驱动
    static {
        try {
            // 解决 JDK 25 + SQL Server 2014 的 TLS 版本不兼容问题
            // SQL Server 2014 只支持 TLS 1.0，需要手动允许
            String disabledAlgorithms = java.security.Security.getProperty("jdk.tls.disabledAlgorithms");
            if (disabledAlgorithms != null && disabledAlgorithms.contains("TLSv1")) {
                disabledAlgorithms = disabledAlgorithms.replace("TLSv1.1", "").replace("TLSv1", "").replace(", ,", ",");
                java.security.Security.setProperty("jdk.tls.disabledAlgorithms", disabledAlgorithms);
            }
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("加载SQL Server驱动失败！");
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭数据库资源（Connection, Statement, ResultSet）
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库资源（无 ResultSet）
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
