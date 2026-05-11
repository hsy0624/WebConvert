<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.entity.User" %>
<%
    // 获取当前登录用户
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>格式转换工具</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: #f0f2f5;
            min-height: 100vh;
        }
        /* ===== 顶部导航栏 ===== */
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
        /* ===== 主体内容 ===== */
        .container {
            max-width: 900px;
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
        /* ===== 转换类型选择 ===== */
        .type-group {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-bottom: 20px;
        }
        .type-group label {
            padding: 8px 16px;
            background: #f0f0f0;
            border-radius: 20px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
            border: 2px solid transparent;
        }
        .type-group input[type="radio"] { display: none; }
        .type-group input[type="radio"]:checked + span {
            background: #667eea;
            color: #fff;
        }
        .type-group label:hover { border-color: #667eea; }
        .type-group label span {
            display: block;
            padding: 8px 16px;
            border-radius: 20px;
            margin: -8px -16px;
        }
        /* ===== 文本区域 ===== */
        .text-area-group {
            display: flex;
            gap: 20px;
            margin-bottom: 20px;
        }
        .text-area-group .col {
            flex: 1;
        }
        .text-area-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: bold;
        }
        .text-area-group textarea {
            width: 100%;
            height: 250px;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            font-family: "Consolas", "Courier New", monospace;
            resize: vertical;
            outline: none;
            transition: border-color 0.3s;
        }
        .text-area-group textarea:focus { border-color: #667eea; }
        /* ===== 按钮 ===== */
        .btn-group {
            text-align: center;
        }
        .btn {
            padding: 12px 40px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: opacity 0.3s;
        }
        .btn:hover { opacity: 0.9; }
        .error { color: red; text-align: center; margin-bottom: 15px; }
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
            <h2>选择转换类型</h2>

            <!-- 显示错误信息 -->
            <% if (request.getAttribute("error") != null) { %>
                <p class="error"><%= request.getAttribute("error") %></p>
            <% } %>

            <!-- 转换表单 -->
            <form action="${pageContext.request.contextPath}/convert" method="post" enctype="multipart/form-data">
                <!-- 转换类型单选按钮 -->
                <div class="type-group">
                    <%
                        String selectedType = (String) request.getAttribute("selectedType");
                        if (selectedType == null) selectedType = "image_convert";
                    %>
                    <label><input type="radio" name="type" value="image_convert" <%= "image_convert".equals(selectedType) ? "checked" : "" %>><span>🖼️ 图片格式转换</span></label>
                    <label><input type="radio" name="type" value="audio_convert" <%= "audio_convert".equals(selectedType) ? "checked" : "" %>><span>🎵 音频格式转换</span></label>
                </div>

                <!-- 文件上传区域（仅在图片/音频转换时显示） -->
                <div id="file-upload-section" style="display: none; margin-bottom: 20px;">
                    <label>📁 选择文件</label>
                    <input type="file" name="uploadFile" accept="image/*,audio/*" required>
                    <p style="font-size: 12px; color: #888; margin-top: 5px;">支持 JPG/PNG/GIF / MP3/WAV/OGG 等常见格式</p>
                </div>

                <!-- 目标格式选择 -->
                <div class="type-group" style="margin-bottom: 20px;">
                    <label><input type="radio" name="targetFormat" value="jpg" checked><span>JPG</span></label>
                    <label><input type="radio" name="targetFormat" value="png"><span>PNG</span></label>
                    <label><input type="radio" name="targetFormat" value="gif"><span>GIF</span></label>
                    <label><input type="radio" name="targetFormat" value="mp3"><span>MP3</span></label>
                    <label><input type="radio" name="targetFormat" value="wav"><span>WAV</span></label>
                </div>

                <!-- 使用说明区域 -->
                <div class="card" style="background:#f8f9fa; border-left:4px solid #667eea; margin-top: 25px;">
                    <h2>💡 使用说明</h2>
                    <ul style="padding-left: 20px; line-height: 1.6; font-size: 14px; color: #555;">
                        <li><strong>图片转换：</strong>上传 JPG/PNG/GIF，选择目标格式（如 PNG → JPG），自动压缩并转换</li>
                        <li><strong>音频转换：</strong>上传 MP3/WAV/OGG，选择目标格式（如 WAV → MP3），自动转码</li>
                        <li>所有转换均在服务端完成，原始文件不会上传到第三方</li>
                        <li>转换记录将保存在「历史记录」中，登录后可随时查看</li>
                    </ul>
                </div>

                <!-- 输入和输出区域 -->
                <div class="text-area-group">
                    <div class="col">
                        <label>📥 输入内容</label>
                        <textarea name="original" placeholder="请在此输入要转换的内容..."><%= request.getAttribute("original") != null ? request.getAttribute("original") : "" %></textarea>
                    </div>
                    <div class="col">
                        <label>📤 转换结果</label>
                        <textarea readonly placeholder="转换结果将显示在这里..."><%= request.getAttribute("result") != null ? request.getAttribute("result") : "" %></textarea>
                    </div>
                </div>

                <div class="btn-group">
                    <button type="submit" class="btn">开始转换</button>
                </div>
            </form>
        </div>
    </div>
    <script>
        // 动态显示/隐藏文件上传区域
        document.querySelectorAll('input[name="type"]').forEach(radio => {
            radio.addEventListener('change', function() {
                const uploadSection = document.getElementById('file-upload-section');
                if (this.value === 'image_convert' || this.value === 'audio_convert') {
                    uploadSection.style.display = 'block';
                } else {
                    uploadSection.style.display = 'none';
                }
            });
        });

        // 初始化状态
        const initType = document.querySelector('input[name="type"]:checked');
        if (initType && (initType.value === 'image_convert' || initType.value === 'audio_convert')) {
            document.getElementById('file-upload-section').style.display = 'block';
        }
    </script>
</body>
</html>
