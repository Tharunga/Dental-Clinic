<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.sunrisedental.model.SystemLog" %>
<%
    List<SystemLog> logs = (List<SystemLog>) request.getAttribute("logs");
    DateTimeFormatter whenFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>System Log | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>System Log</h1>
                <p>Recent staff activity, newest first.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Time</th>
                    <th>User</th>
                    <th>Action</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                <% if (logs == null || logs.isEmpty()) { %>
                <tr>
                    <td colspan="4">No log entries yet.</td>
                </tr>
                <% } else {
                    for (SystemLog log : logs) {
                %>
                <tr>
                    <td><%= log.getCreatedAt() != null ? log.getCreatedAt().format(whenFmt) : "—" %></td>
                    <td><%= log.getUsername() != null ? log.getUsername() : "—" %></td>
                    <td><span class="badge"><%= log.getAction() %></span></td>
                    <td><%= log.getDetails() != null ? log.getDetails() : "—" %></td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>
