package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.example.entity.User;
import org.example.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// 文件上传依赖
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 转换Servlet - 处理各种格式转换请求（核心功能）
 * URL 映射: /convert
 * 支持的转换类型：
 *   1. json_format   - JSON 格式化
 *   2. json_compress - JSON 压缩
 *   3. to_upper      - 转大写
 *   4. to_lower      - 转小写
 *   5. remove_space  - 去除空格与换行
 *   6. ts_to_date    - 时间戳转日期
 *   7. date_to_ts    - 日期转时间戳
 *   8. url_encode    - URL 编码
 *   9. url_decode    - URL 解码
 */
@WebServlet("/convert")
public class ConvertServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 判断是否为 multipart/form-data（文件上传）
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        String type = req.getParameter("type");
        String result = "处理中...";
        String typeName = "文件格式转换";
        String original = "";

        // ===== 处理文件上传请求 =====
        if (isMultipart && ("image_convert".equals(type) || "audio_convert".equals(type))) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setHeaderEncoding("UTF-8");

                // 解析请求（关键：必须捕获 FileUploadException）
                java.util.List<FileItem> items;
                try {
                    items = upload.parseRequest(req);
                } catch (org.apache.commons.fileupload.FileUploadException e) {
                    throw new ServletException("文件上传解析失败，请检查文件大小或格式：" + e.getMessage(), e);
                }

                String fileName = "";
                String targetFormat = "";

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        // 普通字段
                        String fieldName = item.getFieldName();
                        if ("targetFormat".equals(fieldName)) {
                            targetFormat = item.getString("UTF-8");
                        }
                    } else {
                        // 文件字段
                        fileName = new File(item.getName()).getName();
                        if (!fileName.isEmpty()) {
                            // 生成唯一文件名并保存（示例路径，生产环境请用配置）
                            String uploadDir = getServletContext().getRealPath("/uploads");
                            File dir = new File(uploadDir);
                            if (!dir.exists()) dir.mkdirs();

                            String uniqueName = UUID.randomUUID().toString() + "_" + fileName;
                            File uploadedFile = new File(dir, uniqueName);
                            item.write(uploadedFile);

                            // 模拟转换：返回新文件名（实际将调用 ImageIO/JAVE2）
                            String convertedName = fileName.substring(0, fileName.lastIndexOf('.')) + "." + targetFormat;
                            result = "✅ 转换完成！已生成：" + convertedName;
                            original = "上传文件：" + fileName + " → 目标格式：" + targetFormat;
                            typeName = "文件格式转换";
                            break;
                        }
                    }
                }

                if (fileName.isEmpty()) {
                    throw new IllegalArgumentException("未选择文件！");
                }
                if (targetFormat.isEmpty()) {
                    throw new IllegalArgumentException("请选择目标格式！");
                }

            } catch (Exception e) {
                req.setAttribute("error", "文件处理失败：" + e.getMessage());
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }
        }
        // ===== 处理普通文本转换请求 =====
        else {
            original = req.getParameter("original");

            // 参数校验
            if (type == null || original == null || original.isEmpty()) {
                req.setAttribute("error", "请输入要转换的内容！");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }

            // ===== 根据类型执行不同的转换逻辑 =====
            switch (type) {
                case "json_format":
                    typeName = "JSON格式化";
                    result = jsonFormat(original);
                    break;
                case "json_compress":
                    typeName = "JSON压缩";
                    result = jsonCompress(original);
                    break;
                case "to_upper":
                    typeName = "转大写";
                    result = original.toUpperCase();
                    break;
                case "to_lower":
                    typeName = "转小写";
                    result = original.toLowerCase();
                    break;
                case "remove_space":
                    typeName = "去除空格换行";
                    result = original.replaceAll("\\s+", "");
                    break;
                case "ts_to_date":
                    typeName = "时间戳转日期";
                    result = timestampToDate(original);
                    break;
                case "date_to_ts":
                    typeName = "日期转时间戳";
                    result = dateToTimestamp(original);
                    break;
                case "url_encode":
                    typeName = "URL编码";
                    result = URLEncoder.encode(original, StandardCharsets.UTF_8.toString());
                    break;
                case "url_decode":
                    typeName = "URL解码";
                    result = URLDecoder.decode(original, StandardCharsets.UTF_8.toString());
                    break;
                default:
                    typeName = "未知";
                    result = "不支持的转换类型！";
            }
        }

        // ===== 将转换记录保存到数据库 =====
        User user = (User) req.getSession().getAttribute("user");
        if (user != null) {
            saveLog(user.getId(), typeName, original, result);
        }

        // 将结果返回到页面
        req.setAttribute("original", original);
        req.setAttribute("result", result);
        req.setAttribute("selectedType", type);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    /**
     * JSON 格式化（美化输出）
     */
    private String jsonFormat(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object obj = JsonParser.parseString(json);
            return gson.toJson(obj);
        } catch (Exception e) {
            return "JSON格式错误：" + e.getMessage();
        }
    }

    /**
     * JSON 压缩（去除多余空格和换行）
     */
    private String jsonCompress(String json) {
        try {
            Gson gson = new Gson();
            Object obj = JsonParser.parseString(json);
            return gson.toJson(obj);
        } catch (Exception e) {
            return "JSON格式错误：" + e.getMessage();
        }
    }

    /**
     * 时间戳（秒/毫秒）转日期字符串
     */
    private String timestampToDate(String ts) {
        try {
            long timestamp = Long.parseLong(ts.trim());
            // 如果是秒级时间戳（10位），转为毫秒
            if (String.valueOf(timestamp).length() <= 10) {
                timestamp *= 1000;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            return "时间戳格式错误，请输入数字！";
        }
    }

    /**
     * 日期字符串转时间戳（秒）
     */
    private String dateToTimestamp(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(dateStr.trim());
            return String.valueOf(date.getTime() / 1000);
        } catch (Exception e) {
            return "日期格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式！";
        }
    }

    /**
     * 保存转换记录到数据库
     */
    private void saveLog(int userId, String type, String original, String result) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO convert_log (user_id, type, original, result) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, original);
            ps.setString(4, result);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // 保存失败不影响返回结果
        } finally {
            DBUtil.close(conn, ps);
        }
    }
}
