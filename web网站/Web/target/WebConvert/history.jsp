<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.entity.User" %>
<%@ page import="org.example.entity.ConvertLog" %>
<%@ page import="java.util.List" %>
<%
    // 获取当前登录用户
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    // 获取记录列表
    List<ConvertLog> logs = (List<ConvertLog>) request.getAttribute("logs");
    // 是否是管理员视图
    Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
    if (isAdmin == null) isAdmin = false;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= isAdmin ? "管理后台" : "历史记录" %> - 格式转换工具</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: #f0f2f5;
            min-height: 100vh;
        }
        .navbar {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .navbar h1 { font-size: 20px; }
        .navbar .nav-links a {
            color: #fff;
            text-decoration: none;
            margin-left: 20px;
            font-size: 14px;
            padding: 6px 12px;
            border-radius: 4px;
            transition: background 0.3s;
        }
        .navbar .nav-links a:hover { background: rgba(255,255,255,0.2); }
        .container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 0 20px;
        }
        .card {
            background: #fff;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 15px rgba(0,0,0,0.08);
        }
        .card h2 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
        }
        /* ===== 表格样式 ===== */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }
        table th, table td {
            padding: 12px 10px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        table th {
            background: #f8f9fa;
            color: #555;
            font-weight: bold;
        }
        table tr:hover { background: #f5f5ff; }
        /* 内容截断显示 */
        .text-ellipsis {
            max-width: 200px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            display: inline-block;
            vertical-align: middle;
        }
        .btn-delete {
            padding: 4px 12px;
            background: #ff4757;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            text-decoration: none;
        }
        .btn-delete:hover { background: #e84141; }
        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <!-- ===== 导航栏 ===== -->
    <div class="navbar">
        <h1>🔧 格式转换工具</h1>
        <div class="nav-links">
            <span>欢迎，<%= user.getUsername() %></span>
            <a href="${pageContext.request.contextPath}/index.jsp">转换工具</a>
            <a href="${pageContext.request.contextPath}/log?action=list">历史记录</a>
            <% if ("admin".equals(user.getUsername())) { %>
                <a href="${pageContext.request.contextPath}/log?action=admin">管理后台</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/user?action=logout">退出登录</a>
        </div>
    </div>

    <!-- ===== 主体内容 ===== -->
    <div class="container">
        <div class="card">
            <h2><%= isAdmin ? "📊 所有用户转换记录（管理员）" : "📋 我的转换历史" %></h2>

            <% if (logs == null || logs.isEmpty()) { %>
                <div class="empty-msg">暂无转换记录</div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>序号</th>
                            <% if (isAdmin) { %><th>用户</th><% } %>
                            <th>转换类型</th>
                            <th>原始内容</th>
                            <th>转换结果</th>
                            <th>转换时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            int index = 1;
                            for (ConvertLog log : logs) {
                        %>
                        <tr>
                            <td><%= index++ %></td>
                            <% if (isAdmin) { %><td><%= log.getUsername() %></td><% } %>
                            <td><%= log.getType() %></td>
                            <td><span class="text-ellipsis" title="<%= log.getOriginal().replace("\"", "&quot;") %>"><%= log.getOriginal() %></span></td>
                            <td><span class="text-ellipsis" title="<%= log.getResult().replace("\"", "&quot;") %>"><%= log.getResult() %></span></td>
                            <td><%= log.getCreateTime() %></td>
                            <td>
                                <a class="btn-delete"
                                   href="${pageContext.request.contextPath}/log?action=delete&id=<%= log.getId() %>&from=<%= isAdmin ? "admin" : "list" %>"
                                   onclick="return confirm('确定要删除这条记录吗？')">删除</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
</body>
</html>
