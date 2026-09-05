<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String mode = (String) request.getAttribute("mode");
    boolean isEdit = "edit".equals(mode);
    String pageTitle = (String) request.getAttribute("pageTitle");
    String formAction = (String) request.getAttribute("formAction");
    Object treatmentId = request.getAttribute("treatmentId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= pageTitle != null ? pageTitle : "Treatment" %> | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <main class="main">
        <div class="topbar">
            <div>
                <h1><%= pageTitle != null ? pageTitle : "Treatment" %></h1>
                <p><%= isEdit ? "Update the treatment name and price." : "Add a treatment type with its price for appointments and billing." %></p>
            </div>
        </div>
        <jsp:include page="/WEB-INF/jspf/alerts.jsp"/>
        <section class="card">
            <form method="post" action="<%= formAction %>">
                <% if (isEdit && treatmentId != null) { %>
                <input type="hidden" name="id" value="<%= treatmentId %>">
                <% } %>
                <div class="form-grid">
                    <div class="field">
                        <label for="treatmentName">Treatment name</label>
                        <input id="treatmentName" name="treatmentName" value="${treatmentName}" required maxlength="80"
                               placeholder="e.g. Teeth Cleaning">
                    </div>
                    <div class="field">
                        <label for="cost">Cost (LKR)</label>
                        <input id="cost" name="cost" type="number" step="0.01" min="0" value="${cost}" required
                               placeholder="e.g. 5000.00" inputmode="decimal">
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit"><%= isEdit ? "Save changes" : "Add treatment" %></button>
                    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/treatments">Cancel</a>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
