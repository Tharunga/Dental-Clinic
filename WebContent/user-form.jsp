<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String mode = (String) request.getAttribute("mode");
    boolean isEdit = "edit".equals(mode);
    String pageTitle = (String) request.getAttribute("pageTitle");
    String formAction = (String) request.getAttribute("formAction");
    Boolean isAdminAttr = (Boolean) request.getAttribute("isAdmin");
    boolean isAdmin = isAdminAttr != null && isAdminAttr;
    Object userId = request.getAttribute("userId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle != null ? pageTitle : "User" %> | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1><%= pageTitle != null ? pageTitle : "User" %></h1>
                <p><%= isEdit ? "Update account details. Leave password blank to keep the current one." : "Create an admin or receptionist account." %></p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="post" action="<%= formAction %>">
                <% if (isEdit && userId != null) { %>
                <input type="hidden" name="id" value="<%= userId %>">
                <% } %>
                <div class="form-grid">
                    <div class="field">
                        <label for="username">Username</label>
                        <input id="username" name="username" value="${username}" required maxlength="50"
                               pattern="[A-Za-z0-9._-]+" autocomplete="off">
                    </div>
                    <div class="field">
                        <label for="fullName">Full name</label>
                        <input id="fullName" name="fullName" value="${fullName}" required maxlength="100">
                    </div>
                    <div class="field">
                        <label for="password">Password<%= isEdit ? " (optional)" : "" %></label>
                        <input id="password" type="password" name="password"
                               <%= isEdit ? "" : "required" %> minlength="4" autocomplete="new-password">
                    </div>
                    <div class="field">
                        <label for="isAdmin">User type</label>
                        <select id="isAdmin" name="isAdmin" required>
                            <option value="0" <%= !isAdmin ? "selected" : "" %>>Receptionist</option>
                            <option value="1" <%= isAdmin ? "selected" : "" %>>Admin</option>
                        </select>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit"><%= isEdit ? "Save changes" : "Create user" %></button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/users">Cancel</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
