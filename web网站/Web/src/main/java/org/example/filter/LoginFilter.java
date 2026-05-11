package org.example.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录拦截过滤器
 * 检查用户是否已登录，未登录则跳转到登录页
 * 在 web.xml 中配置拦截 /convert、/log、/index.jsp、/history.jsp
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 从 Session 中获取用户信息
        HttpSession session = req.getSession(false);
        Object user = (session != null) ? session.getAttribute("user") : null;

        if (user == null) {
            // 未登录，跳转到登录页
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            // 已登录，放行
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
