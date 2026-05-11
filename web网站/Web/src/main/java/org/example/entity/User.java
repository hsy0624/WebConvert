package org.example.entity;

/**
 * 用户实体类 - 对应数据库 user 表
 */
public class User {
    private int id;           // 用户ID
    private String username;  // 用户名
    private String password;  // 密码
    private String createTime; // 注册时间

    // ===== Getter 和 Setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
