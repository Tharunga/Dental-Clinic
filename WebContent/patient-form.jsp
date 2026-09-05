<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Boolean activeAttr = (Boolean) request.getAttribute("active");
    boolean active = activeAttr == null || activeAttr;
    Object patientId = request.getAttribute("patientId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit patient | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Edit patient</h1>
                <p>Update contact details or set the patient inactive. NIC cannot be changed.</p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="post" action="${pageContext.request.contextPath}/patients/edit">
                <% if (patientId != null) { %>
                <input type="hidden" name="id" value="<%= patientId %>">
                <% } %>
                <div class="form-grid">
                    <div class="field">
                        <label for="nic">NIC number</label>
                        <input id="nic" value="${nic}" readonly disabled>
                    </div>
                    <div class="field">
                        <label for="patientName">Patient name</label>
                        <input id="patientName" name="patientName" value="${patientName}" required maxlength="100">
                    </div>
                    <div class="field">
                        <label for="contactNumber">Contact number</label>
                        <input id="contactNumber" name="contactNumber" value="${contactNumber}" required maxlength="20"
                               placeholder="e.g. 0771234567" inputmode="tel">
                    </div>
                    <div class="field">
                        <label for="isActive">Status</label>
                        <select id="isActive" name="isActive" required>
                            <option value="1" <%= active ? "selected" : "" %>>Active</option>
                            <option value="0" <%= !active ? "selected" : "" %>>Inactive</option>
                        </select>
                    </div>
                    <div class="field" style="grid-column: 1 / -1;">
                        <label for="address">Address</label>
                        <textarea id="address" name="address" required>${address}</textarea>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Save changes</button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/patients">Cancel</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
