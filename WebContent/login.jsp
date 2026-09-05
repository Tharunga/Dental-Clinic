<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff login | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body class="auth-body">
    <div class="auth-card">
        <section class="auth-brand">
            <div>
                <img class="mark" src="${pageContext.request.contextPath}/images/logo.png" alt="Sunrise Dental Clinic">
                <h1>Sunrise Dental Clinic</h1>
                <p>Appointment and patient desk for authorised staff in Colombo.</p>
            </div>
            <p>42 Galle Road, Colombo 03<br>+94 11 234 5678</p>
        </section>
        <section class="auth-form">
            <h2>Staff login</h2>
            <p>Enter your username and password to open the clinic system.</p>
            <% if ("1".equals(request.getParameter("loggedOut"))) { %>
            <div class="alert alert-ok">You have exited the system safely.</div>
            <% } %>
            <% String error = (String) request.getAttribute("error");
               if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="field">
                    <label for="username">Username</label>
                    <input id="username" name="username" value="${username}" autocomplete="username" required>
                </div>
                <div class="field">
                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" autocomplete="current-password" required>
                </div>
                <button class="btn btn-primary" type="submit">Sign in</button>
            </form>
            <p style="margin-top:22px;font-size:0.85rem;">Demo access: <strong>admin / admin123</strong> or <strong>receptionist / rec123</strong></p>
        </section>
    </div>
</body>
</html>
