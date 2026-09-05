<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.sunrisedental.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    String uri = request.getRequestURI();
%>
<aside class="sidebar">
    <div class="brand-mini">
        <img class="mark" src="${pageContext.request.contextPath}/images/logo.png" alt="Sunrise Dental Clinic">
        <div>
            <strong>Sunrise Dental</strong>
            <span>Colombo clinic desk</span>
        </div>
    </div>
    <nav class="nav">
        <a class="<%= uri.contains("dashboard") ? "active" : "" %>" href="${pageContext.request.contextPath}/dashboard">Home</a>
        <a class="<%= uri.contains("register") ? "active" : "" %>" href="${pageContext.request.contextPath}/appointments/register">Register appointment</a>
        <a class="<%= uri.contains("search") || uri.contains("details") ? "active" : "" %>" href="${pageContext.request.contextPath}/appointments/search">Find appointment</a>
        <a class="<%= uri.contains("billing") || uri.contains("bill") ? "active" : "" %>" href="${pageContext.request.contextPath}/billing">Calculate bill</a>
        <a class="<%= uri.contains("help") ? "active" : "" %>" href="${pageContext.request.contextPath}/help">Help</a>
        <a class="exit" href="${pageContext.request.contextPath}/logout">Exit system</a>
    </nav>
    <p style="padding:14px 10px 0;color:#9bbfb6;font-size:0.8rem;">
        Signed in as <%= currentUser != null ? currentUser.getFullName() : "" %>
        (<%= currentUser != null ? currentUser.getRole() : "" %>)
    </p>
</aside>
