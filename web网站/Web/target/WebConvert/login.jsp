<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录 - 格式转换工具</title>
    <style>
        /* ===== 全局样式 ===== */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .login-box {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
            width: 400px;
        }
        .login-box h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-size: 14px;
        }
        .form-group input {
            width: 100%;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            outline: none;
            transition: border-color 0.3s;
        }
        .form-group input:focus {
            border-color: #667eea;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: opacity 0.3s;
        }
        .btn:hover { opacity: 0.9; }
        .msg { color: green; text-align: center; margin-bottom: 15px; font-size: 14px; }
        .error { color: red; text-align: center; margin-bottom: 15px; font-size: 14px; }
        .link {
            text-align: center;
            margin-top: 15px;
            font-size: 14px;
        }
        .link a { color: #667eea; text-decoration: none; }
        .link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>🔧 格式转换工具 - 登录</h2>

        <!-- 显示成功提示（如注册成功） -->
        <% if (request.getAttribute("msg") != null) { %>
            <p class="msg"><%= request.getAttribute("msg") %></p>
        <% } %>
        <!-- 显示错误提示 -->
        <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
        <% } %>

        <!-- 登录表单，提交到 /user?action=login -->
        <form action="${pageContext.request.contextPath}/user" method="post">
            <input type="hidden" name="action" value="login">
            <div class="form-group">
                <label>用户名</label>
                <input type="text" name="username" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
                <label>密码</label>
                <input type="password" name="password" placeholder="请输入密码" required>
            </div>
            <button type="submit" class="btn">登 录</button>
        </form>

        <div class="link">
            还没有账号？<a href="register.jsp">立即注册</a>
        </div>
    </div>
</body>
</html>
