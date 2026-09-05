<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Patient" %>
<%
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    String q = (String) request.getAttribute("q");
    if (q == null) {
        q = "";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Patients | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Patients</h1>
                <p>Search by NIC or name. Edit details or set a patient inactive.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="get" action="${pageContext.request.contextPath}/patients" class="search-bar">
                <div class="field">
                    <label for="q">Search</label>
                    <input id="q" name="q" value="<%= q %>" placeholder="NIC or patient name">
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Search</button>
                    <% if (!q.isEmpty()) { %>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/patients">Clear</a>
                    <% } %>
                </div>
            </form>
        </section>
        <section class="card" style="margin-top:18px;">
            <table class="table">
                <thead>
                <tr>
                    <th>NIC</th>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Address</th>
                    <th>Last visit</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% if (patients == null || patients.isEmpty()) { %>
                <tr>
                    <td colspan="7"><%= q.isEmpty() ? "No patients registered yet." : "No patients match your search." %></td>
                </tr>
                <% } else {
                    for (Patient p : patients) {
                %>
                <tr>
                    <td><%= p.getNic() %></td>
                    <td><%= p.getPatientName() %></td>
                    <td><%= p.getContactNumber() %></td>
                    <td><%= p.getAddress() %></td>
                    <td><%= p.getLastVisitDate() != null ? p.getLastVisitDate() : "—" %></td>
                    <td><span class="badge"><%= p.isActive() ? "Active" : "Inactive" %></span></td>
                    <td>
                        <a class="btn btn-ghost" href="${pageContext.request.contextPath}/patients/edit?id=<%= p.getPatientId() %>">Edit</a>
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
