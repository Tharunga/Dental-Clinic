<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="com.sunrisedental.model.Treatment" %>
<%
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
    List<Treatment> treatments = (List<Treatment>) request.getAttribute("treatments");
    String selDentist = (String) request.getAttribute("dentistId");
    String selTreatment = (String) request.getAttribute("treatmentId");
    String selTime = (String) request.getAttribute("appointmentTime");
    Boolean returning = (Boolean) request.getAttribute("returningPatient");
    String lastVisitDate = (String) request.getAttribute("lastVisitDate");
    String[] times = {"09:00","09:30","10:00","10:30","11:00","11:30","12:00","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00"};
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register appointment | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Register new appointment</h1>
                <p>Enter the patient NIC first. Returning patients are filled in automatically.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>

        <section class="card">
            <form method="get" action="${pageContext.request.contextPath}/appointments/register">
                <div class="form-grid">
                    <div class="field">
                        <label for="lookupNic">Patient NIC</label>
                        <input id="lookupNic" name="nic" value="${nic}" placeholder="123456789V or 12-digit NIC" required>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Look up patient</button>
                </div>
            </form>
        </section>

        <section class="card" style="margin-top:18px;">
            <form method="post" action="${pageContext.request.contextPath}/appointments/register">
                <div class="form-grid">
                    <div class="field">
                        <label for="nic">NIC number</label>
                        <input id="nic" name="nic" value="${nic}" placeholder="123456789V or 12-digit NIC" required>
                    </div>
                    <div class="field">
                        <label for="patientName">Patient name</label>
                        <input id="patientName" name="patientName" value="${patientName}" required>
                    </div>
                    <div class="field">
                        <label for="contactNumber">Contact number</label>
                        <input id="contactNumber" name="contactNumber" value="${contactNumber}" placeholder="0771234567" required>
                    </div>
                    <div class="field" style="grid-column: 1 / -1;">
                        <label for="address">Address</label>
                        <textarea id="address" name="address" required>${address}</textarea>
                    </div>
                    <% if (returning != null) { %>
                    <div class="field" style="grid-column: 1 / -1;">
                        <p style="margin:0;color:var(--muted, #64748b);font-size:0.95rem;">
                            <%= Boolean.TRUE.equals(returning)
                                    ? "Returning patient — details loaded from the database. You can update them if needed."
                                    : "New patient — complete the details below." %>
                            <% if (Boolean.TRUE.equals(returning)) { %>
                            <br>
                            Last visit:
                            <strong><%= (lastVisitDate != null && !lastVisitDate.isEmpty()) ? lastVisitDate : "No previous visits" %></strong>
                            <% } %>
                        </p>
                    </div>
                    <% } %>
                    <div class="field">
                        <label for="dentistId">Dentist</label>
                        <select id="dentistId" name="dentistId" required>
                            <option value="">Select dentist</option>
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
                        <label for="treatmentId">Treatment type</label>
                        <select id="treatmentId" name="treatmentId" required>
                            <option value="">Select treatment</option>
                            <% if (treatments != null) {
                                for (Treatment t : treatments) {
                                    String id = String.valueOf(t.getTreatmentId());
                                    BigDecimal cost = t.getCost();
                            %>
                            <option value="<%= id %>" <%= id.equals(selTreatment) ? "selected" : "" %>>
                                <%= t.getTreatmentName() %> (LKR <%= cost %>)
                            </option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="field">
                        <label for="appointmentDate">Appointment date</label>
                        <input id="appointmentDate" type="date" name="appointmentDate" value="${appointmentDate}" required>
                    </div>
                    <div class="field">
                        <label for="appointmentTime">Appointment time</label>
                        <select id="appointmentTime" name="appointmentTime" required>
                            <option value="">Select time</option>
                            <% for (String t : times) { %>
                            <option value="<%= t %>" <%= t.equals(selTime) ? "selected" : "" %>><%= t %></option>
                            <% } %>
                        </select>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Save appointment</button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/dashboard">Cancel</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
