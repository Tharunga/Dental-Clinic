<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Help | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Staff help</h1>
                <p>Step-by-step guide for new reception staff.</p>
            </div>
        </div>
        <section class="card">
            <div class="help-step">
                <div class="step-no">1</div>
                <div>
                    <h3>Sign in</h3>
                    <p>Open the system and enter the username and password given by the clinic admin. Only authorised staff can continue.</p>
                </div>
            </div>
            <div class="help-step">
                <div class="step-no">2</div>
                <div>
                    <h3>Register a new appointment</h3>
                    <p>Choose Register appointment. Enter the patient NIC and click Look up patient. If the patient is already registered, name, contact and address are filled in automatically. For a new patient, complete those details, then choose dentist, treatment, date and time. The system creates a unique appointment number such as APT20260001. The same dentist cannot be booked twice at the same time.</p>
                </div>
            </div>
            <div class="help-step">
                <div class="step-no">3</div>
                <div>
                    <h3>Find or update a patient</h3>
                    <p>Open Patients and search by NIC or name. Edit contact details or set a patient inactive. Inactive patients cannot book new appointments until reactivated.</p>
                </div>
            </div>
            <div class="help-step">
                <div class="step-no">4</div>
                <div>
                    <h3>Display or edit appointment details</h3>
                    <p>Choose Find appointment and type the appointment number. The full patient and visit record appears on screen. Click Edit appointment to change patient name, contact, address, dentist, treatment, date, time, or status (Booked, Completed, Cancelled).</p>
                </div>
            </div>
            <div class="help-step">
                <div class="step-no">5</div>
                <div>
                    <h3>Calculate and print the bill</h3>
                    <p>Open Calculate bill, enter the appointment number, then click Calculate bill. The total is consultation fee plus treatment cost. Click Confirm &amp; print receipt and accept the confirmation — this marks the appointment Completed and opens the print dialog. Completed visits appear in Find appointment and are counted on the home page.</p>
                </div>
            </div>
            <div class="help-step">
                <div class="step-no">6</div>
                <div>
                    <h3>Log out</h3>
                    <p>Click Logout at the bottom of the sidebar when you leave the desk. Confirm in the dialog so the next person must log in again.</p>
                </div>
            </div>
        </section>
    </main>
</div>
</body>
</html>
