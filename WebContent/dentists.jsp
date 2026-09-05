<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dentists | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Dentists</h1>
                <p>Add and manage dentists available for appointments.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/dentists/new">Add dentist</a>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Specialization</th>
                    <th>Mobile</th>
                    <th>Email</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% if (dentists == null || dentists.isEmpty()) { %>
                <tr>
                    <td colspan="6">No dentists found. Add the first dentist.</td>
                </tr>
                <% } else {
                    for (Dentist d : dentists) {
                %>
                <tr>
                    <td><%= d.getDentistName() %></td>
                    <td><%= d.getSpecialization() %></td>
                    <td><%= d.getMobileNumber() %></td>
                    <td><%= d.getEmail() %></td>
                    <td><span class="badge"><%= d.isActive() ? "Active" : "Inactive" %></span></td>
                    <td>
                        <a class="btn btn-ghost" href="${pageContext.request.contextPath}/dentists/edit?id=<%= d.getDentistId() %>">Edit</a>
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
