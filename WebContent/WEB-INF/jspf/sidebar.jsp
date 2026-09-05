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
    </nav>
    <div class="sidebar-footer">
        <button type="button" class="btn-logout" id="logoutOpenBtn">Logout</button>
        <p class="sidebar-user">
            <%= currentUser != null ? currentUser.getFullName() : "" %>
            <span>(<%= currentUser != null ? currentUser.getRole() : "" %>)</span>
        </p>
    </div>
</aside>

<div class="modal-backdrop" id="logoutModal" aria-hidden="true">
    <div class="modal-card" role="dialog" aria-modal="true" aria-labelledby="logoutModalTitle">
        <h3 id="logoutModalTitle">Log out?</h3>
        <p>Are you sure you want to log out of Sunrise Dental?</p>
        <div class="actions">
            <button type="button" class="btn btn-ghost" id="logoutCancelBtn">Cancel</button>
            <a class="btn btn-primary" id="logoutConfirmBtn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>
</div>

<script>
(function () {
    var modal = document.getElementById("logoutModal");
    var openBtn = document.getElementById("logoutOpenBtn");
    var cancelBtn = document.getElementById("logoutCancelBtn");
    if (!modal || !openBtn || !cancelBtn) return;

    // Keep popup out of the sidebar flex layout
    document.body.appendChild(modal);

    function openModal() {
        modal.classList.add("is-open");
        modal.setAttribute("aria-hidden", "false");
        document.body.classList.add("modal-open");
        cancelBtn.focus();
    }
    function closeModal() {
        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
        document.body.classList.remove("modal-open");
        openBtn.focus();
    }

    openBtn.addEventListener("click", openModal);
    cancelBtn.addEventListener("click", closeModal);
    modal.addEventListener("click", function (e) {
        if (e.target === modal) closeModal();
    });
    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape" && modal.classList.contains("is-open")) closeModal();
    });
})();
</script>
