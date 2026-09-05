<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    User currentUser = (User) session.getAttribute("user");
    DateTimeFormatter whenFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Users</h1>
                <p>Create and manage admin and receptionist accounts.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/users/new">Add user</a>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Full name</th>
                    <th>Username</th>
                    <th>Type</th>
                    <th>Created at</th>
                    <th>Created by</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% if (users == null || users.isEmpty()) { %>
                <tr>
                    <td colspan="6">No users found.</td>
                </tr>
                <% } else {
                    for (User u : users) {
                %>
                <tr>
                    <td><%= u.getFullName() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><span class="badge"><%= u.getRoleLabel() %></span></td>
                    <td><%= u.getCreatedAt() != null ? u.getCreatedAt().format(whenFmt) : "—" %></td>
                    <td><%= u.getCreatedByName() != null ? u.getCreatedByName() : "—" %></td>
                    <td>
                        <div class="actions" style="margin-top:0;">
                            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/users/edit?id=<%= u.getUserId() %>">Edit</a>
                            <% if (currentUser == null || currentUser.getUserId() != u.getUserId()) { %>
                            <form method="post" action="${pageContext.request.contextPath}/users/delete"
                                  onsubmit="return confirm('Delete user <%= u.getUsername() %>?');"
                                  style="display:inline;">
                                <input type="hidden" name="id" value="<%= u.getUserId() %>">
                                <button class="btn btn-danger" type="submit">Delete</button>
                            </form>
                            <% } %>
                        </div>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>
