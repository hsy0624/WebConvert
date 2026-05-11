package org.example.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 编码过滤器 - 统一设置请求和响应的字符编码为 UTF-8
 * 防止中文乱码问题
 */
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器初始化
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求和响应编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // 放行请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 过滤器销毁
    }
}
