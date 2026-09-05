<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%
    Appointment a = (Appointment) request.getAttribute("appointment");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Appointment details | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Display appointment details</h1>
                <p>Search by the unique appointment number.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="get" action="${pageContext.request.contextPath}/appointments/search">
                <div class="form-grid">
                    <div class="field">
                        <label for="number">Appointment number</label>
                        <input id="number" name="number" value="${number}" placeholder="APT20260001" required>
                    </div>
                </div>
                <button class="btn btn-primary" type="submit">Search</button>
            </form>
        </section>
        <% if (a != null) { %>
        <section class="card" style="margin-top:18px;">
            <h2><%= a.getAppointmentNumber() %></h2>
            <p>Complete patient and visit record</p>
            <div class="details">
                <div><span>NIC</span><strong><%= a.getNic() %></strong></div>
                <div><span>Patient name</span><strong><%= a.getPatientName() %></strong></div>
                <div><span>Contact number</span><strong><%= a.getContactNumber() %></strong></div>
                <div style="grid-column:1/-1;"><span>Address</span><strong><%= a.getAddress() %></strong></div>
                <div><span>Dentist</span><strong><%= a.getDentistName() %></strong></div>
                <div><span>Treatment type</span><strong><%= a.getTreatmentName() %></strong></div>
                <div><span>Date</span><strong><%= a.getAppointmentDate() %></strong></div>
                <div><span>Time</span><strong><%= a.getAppointmentTime() %></strong></div>
                <div><span>Status</span><strong><%= a.getStatus() %></strong></div>
            </div>
            <div class="actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/billing?number=<%= a.getAppointmentNumber() %>">Calculate bill</a>
            </div>
        </section>
        <% } %>
    </main>
</div>
</body>
</html>
