<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    List<Appointment> recent = (List<Appointment>) request.getAttribute("recentAppointments");
    Integer total = (Integer) request.getAttribute("totalAppointments");
    Integer today = (Integer) request.getAttribute("todayAppointments");
    Integer completed = (Integer) request.getAttribute("completedAppointments");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Good to see you, <%= user.getFullName() %></h1>
                <p>Use the menu to register visits, look up records, and print bills.</p>
            </div>
            <div class="chip">Sunrise Dental · Colombo</div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="grid-3">
            <article class="card stat">
                <div>
                    <p>All appointments</p>
                    <div class="num"><%= total == null ? 0 : total %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Appointments today</p>
                    <div class="num"><%= today == null ? 0 : today %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Completed</p>
                    <div class="num"><%= completed == null ? 0 : completed %></div>
                </div>
            </article>
        </section>
        <section class="card" style="margin-top:18px;">
            <p>Quick actions</p>
            <div class="actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/appointments/register">New appointment</a>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/billing">Print bill</a>
            </div>
        </section>
        <section class="card" style="margin-top:18px;">
            <h2>Upcoming appointments</h2>
            <table class="table">
                <thead>
                <tr>
                    <th>Number</th>
                    <th>Patient</th>
                    <th>Dentist</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <% if (recent == null || recent.isEmpty()) { %>
                <tr><td colspan="6">No appointments yet. Register the first patient visit.</td></tr>
                <% } else {
                    for (Appointment a : recent) { %>
                <tr>
                    <td><a href="${pageContext.request.contextPath}/appointments/search?number=<%= a.getAppointmentNumber() %>"><%= a.getAppointmentNumber() %></a></td>
                    <td><%= a.getPatientName() %></td>
                    <td><%= a.getDentistName() %></td>
                    <td><%= a.getAppointmentDate() %></td>
                    <td><%= a.getAppointmentTime() %></td>
                    <td><span class="badge<%
                        if ("COMPLETED".equalsIgnoreCase(a.getStatus())) {
                            out.print(" badge-completed");
                        } else if ("CANCELLED".equalsIgnoreCase(a.getStatus())) {
                            out.print(" badge-cancelled");
                        }
                    %>"><%= a.getStatus() %></span></td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>
