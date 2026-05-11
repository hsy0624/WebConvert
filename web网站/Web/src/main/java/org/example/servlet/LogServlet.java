package org.example.servlet;

import org.example.entity.ConvertLog;
import org.example.entity.User;
import org.example.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录Servlet - 处理转换记录的查询和删除
 * URL 映射: /log
 * 通过 action 参数区分操作：
 *   list   - 查看当前用户的历史记录
 *   delete - 删除单条记录
 *   admin  - 管理员查看所有记录
 */
@WebServlet("/log")
public class LogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                deleteLog(req, resp);   // 删除记录
                break;
            case "admin":
                adminList(req, resp);   // 管理员查看所有记录
                break;
            default:
                listMyLogs(req, resp);  // 查看我的记录
                break;
        }
    }

    /**
     * 查看当前用户的转换历史记录
     */
    private void listMyLogs(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        List<ConvertLog> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 按时间倒序查询当前用户的记录
            ps = conn.prepareStatement(
                    "SELECT * FROM convert_log WHERE user_id = ? ORDER BY create_time DESC");
            ps.setInt(1, user.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                ConvertLog log = new ConvertLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setType(rs.getString("type"));
                log.setOriginal(rs.getString("original"));
                log.setResult(rs.getString("result"));
                log.setCreateTime(rs.getString("create_time"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        // 将记录列表传给 JSP 页面
        req.setAttribute("logs", logs);
        req.getRequestDispatcher("/history.jsp").forward(req, resp);
    }

    /**
     * 删除单条转换记录
     */
    private void deleteLog(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String idStr = req.getParameter("id");

        if (user == null || idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            // 只能删除自己的记录（安全校验），管理员可删除所有
            String sql;
            if ("admin".equals(user.getUsername())) {
                sql = "DELETE FROM convert_log WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
            } else {
                sql = "DELETE FROM convert_log WHERE id = ? AND user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
                ps.setInt(2, user.getId());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }

        // 删除后，判断跳转回哪个页面
        String from = req.getParameter("from");
        if ("admin".equals(from)) {
            resp.sendRedirect(req.getContextPath() + "/log?action=admin");
        } else {
            resp.sendRedirect(req.getContextPath() + "/log?action=list");
        }
    }

    /**
     * 管理员查看所有用户的转换记录
     */
    private void adminList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        // 只有 admin 用户可以访问
        if (user == null || !"admin".equals(user.getUsername())) {
            req.setAttribute("error", "无权限访问！");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        List<ConvertLog> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 关联查询用户名，按时间倒序
            ps = conn.prepareStatement(
                    "SELECT c.*, u.username FROM convert_log c " +
                    "LEFT JOIN [user] u ON c.user_id = u.id " +
                    "ORDER BY c.create_time DESC");
            rs = ps.executeQuery();

            while (rs.next()) {
                ConvertLog log = new ConvertLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setType(rs.getString("type"));
                log.setOriginal(rs.getString("original"));
                log.setResult(rs.getString("result"));
                log.setCreateTime(rs.getString("create_time"));
                log.setUsername(rs.getString("username"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        req.setAttribute("logs", logs);
        req.setAttribute("isAdmin", true);
        req.getRequestDispatcher("/history.jsp").forward(req, resp);
    }
}
