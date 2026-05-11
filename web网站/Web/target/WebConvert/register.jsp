<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>注册 - 格式转换工具</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .register-box {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
            width: 400px;
        }
        .register-box h2 {
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
    <div class="register-box">
        <h2>📝 格式转换工具 - 注册</h2>

        <!-- 显示错误提示 -->
        <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
        <% } %>

        <!-- 注册表单，提交到 /user?action=register -->
        <form action="${pageContext.request.contextPath}/user" method="post">
            <input type="hidden" name="action" value="register">
            <div class="form-group">
                <label>用户名</label>
                <input type="text" name="username" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
                <label>密码</label>
                <input type="password" name="password" placeholder="请输入密码" required>
            </div>
            <div class="form-group">
                <label>确认密码</label>
                <input type="password" id="confirmPwd" placeholder="请再次输入密码" required>
            </div>
            <button type="submit" class="btn" onclick="return checkPwd()">注 册</button>
        </form>

        <div class="link">
            已有账号？<a href="login.jsp">去登录</a>
        </div>
    </div>

    <script>
        // 前端校验两次密码是否一致
        function checkPwd() {
            var pwd = document.querySelector('input[name="password"]').value;
            var confirmPwd = document.getElementById('confirmPwd').value;
            if (pwd !== confirmPwd) {
                alert('两次输入的密码不一致！');
                return false;
            }
            return true;
        }
    </script>
</body>
</html>
