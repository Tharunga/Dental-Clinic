<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.util.ClinicConfig" %>
<%
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    User currentUser = (User) session.getAttribute("user");
    boolean isAdmin = currentUser != null && currentUser.isAdmin();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Treatment Types | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Treatment Types</h1>
                <p>View treatment names and prices used for appointments and billing.</p>
            </div>
            <% if (isAdmin) { %>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/treatments/new">Add treatment</a>
            <% } %>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <table class="table">
                <thead>
                <tr>
                    <th>Treatment name</th>
                    <th>Cost</th>
                    <% if (isAdmin) { %>
                    <th>Actions</th>
                    <% } %>
                </tr>
                </thead>
                <tbody>
                <% if (treatments == null || treatments.isEmpty()) { %>
                <tr>
                    <td colspan="<%= isAdmin ? 3 : 2 %>">No treatments found.<% if (isAdmin) { %> Add the first treatment.<% } %></td>
                </tr>
                <% } else {
                    for (Treatment t : treatments) {
                        BigDecimal cost = t.getCost();
                        // Consultation Only stores 0.00; fee is billed via ClinicConfig — show that amount for clarity
                        boolean consultationOnly = "Consultation Only".equals(t.getTreatmentName());
                        BigDecimal displayCost = consultationOnly ? ClinicConfig.CONSULTATION_FEE
                                : (cost != null ? cost : BigDecimal.ZERO);
                %>
                <tr>
                    <td><%= t.getTreatmentName() %></td>
                    <td>LKR <%= displayCost %></td>
                    <% if (isAdmin) { %>
                    <td>
                        <a class="btn btn-ghost" href="${pageContext.request.contextPath}/treatments/edit?id=<%= t.getTreatmentId() %>">Edit</a>
                    </td>
                    <% } %>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>
