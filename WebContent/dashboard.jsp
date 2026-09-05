<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.NamedMetric" %>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    Integer appointmentsToday = (Integer) request.getAttribute("appointmentsToday");
    Integer bookedToday = (Integer) request.getAttribute("bookedToday");
    Integer completedToday = (Integer) request.getAttribute("completedToday");
    Integer cancelledThisMonth = (Integer) request.getAttribute("cancelledThisMonth");
    Integer activePatients = (Integer) request.getAttribute("activePatients");
    BigDecimal revenueToday = (BigDecimal) request.getAttribute("revenueToday");
    BigDecimal revenueThisMonth = (BigDecimal) request.getAttribute("revenueThisMonth");
    BigDecimal avgBillThisMonth = (BigDecimal) request.getAttribute("avgBillThisMonth");
    List<Appointment> todaySchedule = (List<Appointment>) request.getAttribute("todaySchedule");
    List<Appointment> upcoming = (List<Appointment>) request.getAttribute("upcomingAppointments");
    List<NamedMetric> dentistWorkload = (List<NamedMetric>) request.getAttribute("dentistWorkload");

    String chartAptLabels = (String) request.getAttribute("chartAptLabels");
    String chartAptCounts = (String) request.getAttribute("chartAptCounts");
    String chartRevLabels = (String) request.getAttribute("chartRevLabels");
    String chartRevAmounts = (String) request.getAttribute("chartRevAmounts");
    String chartStatusLabels = (String) request.getAttribute("chartStatusLabels");
    String chartStatusCounts = (String) request.getAttribute("chartStatusCounts");
    String chartTreatmentLabels = (String) request.getAttribute("chartTreatmentLabels");
    String chartTreatmentAmounts = (String) request.getAttribute("chartTreatmentAmounts");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Good to see you, <%= user.getFullName() %></h1>
                <p>Clinic overview — today’s schedule, revenue, and workload.</p>
            </div>
            <div class="chip">Sunrise Dental · Colombo</div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>

        <section class="grid-4">
            <article class="card stat">
                <div>
                    <p>Appointments today</p>
                    <div class="num"><%= appointmentsToday == null ? 0 : appointmentsToday %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Booked remaining today</p>
                    <div class="num"><%= bookedToday == null ? 0 : bookedToday %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Completed today</p>
                    <div class="num"><%= completedToday == null ? 0 : completedToday %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Cancelled this month</p>
                    <div class="num"><%= cancelledThisMonth == null ? 0 : cancelledThisMonth %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Revenue today</p>
                    <div class="num num-sm">LKR <%= revenueToday == null ? "0.00" : revenueToday %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Revenue this month</p>
                    <div class="num num-sm">LKR <%= revenueThisMonth == null ? "0.00" : revenueThisMonth %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Avg bill (month)</p>
                    <div class="num num-sm">LKR <%= avgBillThisMonth == null ? "0.00" : avgBillThisMonth %></div>
                </div>
            </article>
            <article class="card stat">
                <div>
                    <p>Active patients</p>
                    <div class="num"><%= activePatients == null ? 0 : activePatients %></div>
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

        <section class="grid-2" style="margin-top:18px;">
            <article class="card chart-card">
                <h2>Appointments · last 7 days</h2>
                <div class="chart-wrap">
                    <canvas id="chartAppointments"></canvas>
                </div>
            </article>
            <article class="card chart-card">
                <h2>Status mix · this month</h2>
                <div class="chart-wrap chart-wrap-pie">
                    <canvas id="chartStatus"></canvas>
                </div>
            </article>
        </section>

        <section class="grid-2" style="margin-top:18px;">
            <article class="card chart-card">
                <h2>Revenue · last 7 days</h2>
                <div class="chart-wrap">
                    <canvas id="chartRevenue"></canvas>
                </div>
            </article>
            <article class="card chart-card">
                <h2>Revenue by treatment · this month</h2>
                <div class="chart-wrap chart-wrap-pie">
                    <canvas id="chartTreatments"></canvas>
                </div>
            </article>
        </section>

        <section class="grid-2" style="margin-top:18px;">
            <article class="card">
                <h2>Today’s schedule</h2>
                <table class="table">
                    <thead>
                    <tr>
                        <th>Time</th>
                        <th>Patient</th>
                        <th>Dentist</th>
                        <th>Treatment</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (todaySchedule == null || todaySchedule.isEmpty()) { %>
                    <tr><td colspan="5">No appointments scheduled for today.</td></tr>
                    <% } else {
                        for (Appointment a : todaySchedule) { %>
                    <tr>
                        <td><%= a.getAppointmentTime() %></td>
                        <td><a href="${pageContext.request.contextPath}/appointments/search?number=<%= a.getAppointmentNumber() %>"><%= a.getPatientName() %></a></td>
                        <td><%= a.getDentistName() %></td>
                        <td><%= a.getTreatmentName() %></td>
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
            </article>
            <article class="card">
                <h2>Dentist workload · this month</h2>
                <table class="table">
                    <thead>
                    <tr>
                        <th>Dentist</th>
                        <th>Booked</th>
                        <th>Done</th>
                        <th>Cancelled</th>
                        <th>Total</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (dentistWorkload == null || dentistWorkload.isEmpty()) { %>
                    <tr><td colspan="5">No active dentists.</td></tr>
                    <% } else {
                        for (NamedMetric m : dentistWorkload) { %>
                    <tr>
                        <td><%= m.getLabel() %></td>
                        <td><%= m.getBooked() %></td>
                        <td><%= m.getCompleted() %></td>
                        <td><%= m.getCancelled() %></td>
                        <td><%= m.getCount() %></td>
                    </tr>
                    <% } } %>
                    </tbody>
                </table>
            </article>
        </section>

        <section class="card" style="margin-top:18px;">
            <h2>Upcoming booked</h2>
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
                <% if (upcoming == null || upcoming.isEmpty()) { %>
                <tr><td colspan="6">No upcoming booked appointments.</td></tr>
                <% } else {
                    for (Appointment a : upcoming) { %>
                <tr>
                    <td><a href="${pageContext.request.contextPath}/appointments/search?number=<%= a.getAppointmentNumber() %>"><%= a.getAppointmentNumber() %></a></td>
                    <td><%= a.getPatientName() %></td>
                    <td><%= a.getDentistName() %></td>
                    <td><%= a.getAppointmentDate() %></td>
                    <td><%= a.getAppointmentTime() %></td>
                    <td><span class="badge"><%= a.getStatus() %></span></td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </section>
    </main>
</div>
<script>
(function () {
    var teal = '#0f6e63';
    var tealSoft = 'rgba(15, 110, 99, 0.55)';
    var gold = '#c48a3a';
    var goldSoft = 'rgba(196, 138, 58, 0.55)';
    var palette = ['#0f6e63', '#c48a3a', '#1f7a4d', '#5c6d66', '#16423b', '#b42318', '#8a6a3b', '#2a8f7f'];

    function emptyNote(canvasId, message) {
        var canvas = document.getElementById(canvasId);
        if (!canvas) return;
        var wrap = canvas.parentElement;
        wrap.innerHTML = '<p class="chart-empty">' + message + '</p>';
    }

    var aptLabels = <%= chartAptLabels == null ? "[]" : chartAptLabels %>;
    var aptCounts = <%= chartAptCounts == null ? "[]" : chartAptCounts %>;
    var revLabels = <%= chartRevLabels == null ? "[]" : chartRevLabels %>;
    var revAmounts = <%= chartRevAmounts == null ? "[]" : chartRevAmounts %>;
    var statusLabels = <%= chartStatusLabels == null ? "[]" : chartStatusLabels %>;
    var statusCounts = <%= chartStatusCounts == null ? "[]" : chartStatusCounts %>;
    var treatmentLabels = <%= chartTreatmentLabels == null ? "[]" : chartTreatmentLabels %>;
    var treatmentAmounts = <%= chartTreatmentAmounts == null ? "[]" : chartTreatmentAmounts %>;

    if (document.getElementById('chartAppointments')) {
        new Chart(document.getElementById('chartAppointments'), {
            type: 'bar',
            data: {
                labels: aptLabels,
                datasets: [{
                    label: 'Appointments',
                    data: aptCounts,
                    backgroundColor: tealSoft,
                    borderColor: teal,
                    borderWidth: 1,
                    borderRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, ticks: { precision: 0 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    if (statusLabels.length === 0) {
        emptyNote('chartStatus', 'No appointments this month yet.');
    } else {
        new Chart(document.getElementById('chartStatus'), {
            type: 'doughnut',
            data: {
                labels: statusLabels,
                datasets: [{
                    data: statusCounts,
                    backgroundColor: palette.slice(0, statusLabels.length),
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }

    if (document.getElementById('chartRevenue')) {
        new Chart(document.getElementById('chartRevenue'), {
            type: 'bar',
            data: {
                labels: revLabels,
                datasets: [{
                    label: 'Revenue (LKR)',
                    data: revAmounts,
                    backgroundColor: goldSoft,
                    borderColor: gold,
                    borderWidth: 1,
                    borderRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    if (treatmentLabels.length === 0) {
        emptyNote('chartTreatments', 'No billed treatments this month yet.');
    } else {
        new Chart(document.getElementById('chartTreatments'), {
            type: 'doughnut',
            data: {
                labels: treatmentLabels,
                datasets: [{
                    data: treatmentAmounts,
                    backgroundColor: palette.slice(0, treatmentLabels.length),
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }
})();
</script>
</body>
</html>
