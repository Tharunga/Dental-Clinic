<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String mode = (String) request.getAttribute("mode");
    boolean isEdit = "edit".equals(mode);
    String pageTitle = (String) request.getAttribute("pageTitle");
    String formAction = (String) request.getAttribute("formAction");
    Object dentistId = request.getAttribute("dentistId");
    Boolean activeAttr = (Boolean) request.getAttribute("active");
    boolean active = activeAttr == null || activeAttr;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle != null ? pageTitle : "Dentist" %> | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1><%= pageTitle != null ? pageTitle : "Dentist" %></h1>
                <p><%= isEdit ? "Update dentist details, contact information, and status." : "Register a dentist so staff can book appointments with them." %></p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="post" action="<%= formAction %>">
                <% if (isEdit && dentistId != null) { %>
                <input type="hidden" name="id" value="<%= dentistId %>">
                <% } %>
                <div class="form-grid">
                    <div class="field">
                        <label for="dentistName">Dentist name</label>
                        <input id="dentistName" name="dentistName" value="${dentistName}" required maxlength="100"
                               placeholder="e.g. Dr. Nimal Perera">
                    </div>
                    <div class="field">
                        <label for="specialization">Specialization</label>
                        <input id="specialization" name="specialization" value="${specialization}" required maxlength="80"
                               placeholder="e.g. Orthodontics">
                    </div>
                    <div class="field">
                        <label for="mobileNumber">Mobile number</label>
                        <input id="mobileNumber" name="mobileNumber" value="${mobileNumber}" required maxlength="20"
                               placeholder="e.g. 0771234567" inputmode="tel">
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" name="email" type="email" value="${email}" required maxlength="100"
                               placeholder="e.g. dentist@sunrise.lk">
                    </div>
                    <div class="field">
                        <label for="isActive">Status</label>
                        <select id="isActive" name="isActive" required>
                            <option value="1" <%= active ? "selected" : "" %>>Active</option>
                            <option value="0" <%= !active ? "selected" : "" %>>Inactive</option>
                        </select>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit"><%= isEdit ? "Save changes" : "Add dentist" %></button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/dentists">Cancel</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
