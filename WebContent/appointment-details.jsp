<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%
    Appointment a = (Appointment) request.getAttribute("appointment");
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    String selDentist = (String) request.getAttribute("dentistId");
    if (selDentist == null) {
        selDentist = "";
    }
    String dateVal = (String) request.getAttribute("date");
    if (dateVal == null) {
        dateVal = "";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Find appointments | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Find appointments</h1>
                <p>Browse all visits; filter by date or dentist, or look up a number.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="get" action="${pageContext.request.contextPath}/appointments/search">
                <div class="form-grid">
                    <div class="field">
                        <label for="date">Date</label>
                        <input id="date" type="date" name="date" value="<%= dateVal %>">
                    </div>
                    <div class="field">
                        <label for="dentistId">Dentist</label>
                        <select id="dentistId" name="dentistId">
                            <option value="">All dentists</option>
                            <% if (dentists != null) {
                                for (Dentist d : dentists) {
                                    String id = String.valueOf(d.getDentistId());
                            %>
                            <option value="<%= id %>" <%= id.equals(selDentist) ? "selected" : "" %>>
                                <%= d.getDentistName() %> — <%= d.getSpecialization() %>
                            </option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="field">
                        <label for="number">Appointment number</label>
                        <input id="number" name="number" value="${number}" placeholder="APT20260001">
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Search</button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/appointments/search">Clear</a>
                </div>
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
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/appointments/edit?number=<%= a.getAppointmentNumber() %>">Edit appointment</a>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/billing?number=<%= a.getAppointmentNumber() %>">Calculate bill</a>
            </div>
        </section>
        <% } %>
        <section class="card" style="margin-top:18px;">
            <h2>Appointments</h2>
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
                <% if (appointments == null || appointments.isEmpty()) { %>
                <tr><td colspan="6">No appointments match the selected filters.</td></tr>
                <% } else {
                    for (Appointment row : appointments) { %>
                <tr>
                    <td><a href="${pageContext.request.contextPath}/appointments/search?number=<%= row.getAppointmentNumber() %>"><%= row.getAppointmentNumber() %></a></td>
                    <td><%= row.getPatientName() %></td>
                    <td><%= row.getDentistName() %></td>
                    <td><%= row.getAppointmentDate() %></td>
                    <td><%= row.getAppointmentTime() %></td>
                    <td><span class="badge<%
                        if ("COMPLETED".equalsIgnoreCase(row.getStatus())) {
                            out.print(" badge-completed");
                        } else if ("CANCELLED".equalsIgnoreCase(row.getStatus())) {
                            out.print(" badge-cancelled");
                        }
                    %>"><%= row.getStatus() %></span></td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>
