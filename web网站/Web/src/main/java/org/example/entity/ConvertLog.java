package org.example.entity;

/**
 * 转换记录实体类 - 对应数据库 convert_log 表
 */
public class ConvertLog {
    private int id;            // 记录ID
    private int userId;        // 用户ID
    private String type;       // 转换类型（如：JSON格式化、大小写转换等）
    private String original;   // 原始内容
    private String result;     // 转换结果
    private String createTime; // 转换时间
    private String username;   // 用户名（关联查询时使用，表中不存在）

    // ===== Getter 和 Setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
