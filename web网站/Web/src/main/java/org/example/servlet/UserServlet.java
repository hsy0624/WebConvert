package org.example.servlet;

import org.example.entity.User;
import org.example.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

/**
 * 用户Servlet - 处理注册、登录、退出请求
 * URL 映射: /user
 * 通过 action 参数区分不同操作
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("logout".equals(action)) {
            // ===== 退出登录：销毁 Session 并跳转到登录页 =====
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("register".equals(action)) {
            register(req, resp);   // 注册
        } else if ("login".equals(action)) {
            login(req, resp);      // 登录
        }
    }

    /**
     * 用户注册
     */
    private void register(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 参数校验
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "用户名和密码不能为空！");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            // 检查用户名是否已存在
            ps = conn.prepareStatement("SELECT id FROM [user] WHERE username = ?");
            ps.setString(1, username.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                req.setAttribute("error", "用户名已存在，请换一个！");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }
            DBUtil.close(null, ps, rs);

            // 插入新用户
            ps = conn.prepareStatement("INSERT INTO [user] (username, password) VALUES (?, ?)");
            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.executeUpdate();

            // 注册成功，跳转到登录页
            req.setAttribute("msg", "注册成功，请登录！");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "系统错误，请稍后重试！");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 用户登录
     */
    private void login(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 参数校验
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "用户名和密码不能为空！");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            // 查询用户
            ps = conn.prepareStatement("SELECT * FROM [user] WHERE username = ? AND password = ?");
            ps.setString(1, username.trim());
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                // 登录成功：创建 User 对象，存入 Session
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setCreateTime(rs.getString("create_time"));

                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                // 跳转到主页
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            } else {
                // 登录失败
                req.setAttribute("error", "用户名或密码错误！");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "系统错误，请稍后重试！");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }
}
