<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.Bill" %>
<%@ page import="com.sunrisedental.util.ClinicConfig" %>
<%
    Appointment a = (Appointment) request.getAttribute("appointment");
    Bill bill = (Bill) request.getAttribute("bill");
    BigDecimal consult = (BigDecimal) request.getAttribute("consultationFee");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Patient bill | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar no-print">
            <div>
                <h1>Calculate and print bill</h1>
                <p>Total = consultation fee (LKR <%= consult %>) + treatment cost.</p>
            </div>
        </div>
        <div class="no-print">
            <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
            <section class="card">
                <form method="post" action="${pageContext.request.contextPath}/billing">
                    <div class="form-grid">
                        <div class="field">
                            <label for="number">Appointment number</label>
                            <input id="number" name="number" value="${number}" placeholder="APT20260001" required>
                        </div>
                    </div>
                    <button class="btn btn-primary" type="submit">Calculate bill</button>
                </form>
            </section>
        </div>
        <% if (bill != null && a != null) { %>
        <section class="receipt" style="margin-top:18px;">
            <h2><%= ClinicConfig.CLINIC_NAME %></h2>
            <div class="meta">
                <%= ClinicConfig.CLINIC_ADDRESS %><br>
                <%= ClinicConfig.CLINIC_PHONE %><br>
                Receipt <%= bill.getBillNumber() %>
            </div>
            <div class="details">
                <div><span>Appointment</span><strong><%= a.getAppointmentNumber() %></strong></div>
                <div><span>Patient</span><strong><%= a.getPatientName() %></strong></div>
                <div><span>Contact</span><strong><%= a.getContactNumber() %></strong></div>
                <div><span>Dentist</span><strong><%= a.getDentistName() %></strong></div>
                <div><span>Date / time</span><strong><%= a.getAppointmentDate() %> · <%= a.getAppointmentTime() %></strong></div>
                <div><span>Treatment</span><strong><%= a.getTreatmentName() %></strong></div>
            </div>
            <div class="totals">
                <div class="row"><span>Consultation fee</span><span>LKR <%= bill.getConsultationFee() %></span></div>
                <div class="row"><span>Treatment cost</span><span>LKR <%= bill.getTreatmentCost() %></span></div>
                <div class="row grand"><span>Total payable</span><span>LKR <%= bill.getTotalAmount() %></span></div>
            </div>
            <p style="text-align:center;margin-top:24px;">Thank you for visiting Sunrise Dental Clinic.</p>
            <div class="actions no-print" style="justify-content:center;">
                <button class="btn btn-print" type="button" onclick="window.print()">Print receipt</button>
            </div>
        </section>
        <% } %>
    </main>
</div>
</body>
</html>
